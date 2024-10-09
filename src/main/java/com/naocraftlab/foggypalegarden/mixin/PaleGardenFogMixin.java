package com.naocraftlab.foggypalegarden.mixin;

import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogCharacteristics;
import com.naocraftlab.foggypalegarden.domain.model.Weather;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import lombok.val;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.RaycastContext;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.naocraftlab.foggypalegarden.domain.model.Weather.CLEAR;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.RAIN;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.THUNDER;
import static net.minecraft.client.render.FogShape.SPHERE;
import static net.minecraft.world.LightType.SKY;
import static net.minecraft.world.RaycastContext.FluidHandling.NONE;
import static net.minecraft.world.RaycastContext.ShapeType.COLLIDER;

@Mixin(BackgroundRenderer.class)
public abstract class PaleGardenFogMixin {

    @Unique
    private static float fogDensity = 0.0f;

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void injectApplyFog(
            Camera camera,
            FogType fogType,
            Vector4f color,
            float viewDistance,
            boolean thickenFog,
            float tickDelta,
            CallbackInfoReturnable<Fog> cir
    ) {
        val focusedEntity = camera.getFocusedEntity();
        val world = (ClientWorld) focusedEntity.getWorld();
        val blockPos = camera.getBlockPos();
        val biomeEntry = world.getBiome(blockPos);
        val hitResult = world.raycast(new RaycastContext(
                blockPos.toCenterPos(), blockPos.add(0, -256, 0).toCenterPos(), COLLIDER, NONE, focusedEntity
        ));

        val fogCharacteristics = FogService.calculateFogCharacteristics(
                Environment.builder()
                        .dimension(world.getRegistryKey().getValue().toString())
                        .biome(biomeEntry.getIdAsString())
                        .difficulty(world.getDifficulty())
                        .weather(resolveWeather(world))
                        .timeOfDay(world.getTimeOfDay())
                        .skyLightLevel(world.getLightLevel(SKY, blockPos))
                        .heightAboveSurface(blockPos.getY() - hitResult.getPos().y)
                        .gameFogColor(toColor(color))
                        .fogDensity(fogDensity)
                        .build()
        );

        assert fogCharacteristics.fogDensity() >= 0.0f : "FPG: Fog density is negative";
        assert fogCharacteristics.fogDensity() <= 1.0f : "FPG: Fog density is greater than 1.0";

        fogDensity = fogCharacteristics.fogDensity();
        if (fogDensity > 0.0f) {
            assert fogCharacteristics.startDistance() >= 0.0f : "FPG: Start distance is negative";
            assert fogCharacteristics.endDistance() >= fogCharacteristics.startDistance()
                    : "FPG: End distance is less than start distance";
            assert fogCharacteristics.color().red() >= 0.0f : "FPG: Red color component is negative";
            assert fogCharacteristics.color().red() <= 1.0f : "FPG: Red color component is greater than 1.0";
            assert fogCharacteristics.color().green() >= 0.0f : "FPG: Green color component is negative";
            assert fogCharacteristics.color().green() <= 1.0f : "FPG: Green color component is greater than 1.0";
            assert fogCharacteristics.color().blue() >= 0.0f : "FPG: Blue color component is negative";
            assert fogCharacteristics.color().blue() <= 1.0f : "FPG: Blue color component is greater than 1.0";
            assert fogCharacteristics.color().alpha() >= 0.0f : "FPG: Alpha color component is negative";
            assert fogCharacteristics.color().alpha() <= 1.0f : "FPG: Alpha color component is greater than 1.0";

            cir.setReturnValue(fogOf(fogCharacteristics));
            cir.cancel();
        }
    }

    @Unique
    private static Weather resolveWeather(ClientWorld world) {
        if (world.isThundering()) {
            return THUNDER;
        } else if (world.isRaining()) {
            return RAIN;
        } else {
            return CLEAR;
        }
    }

    @Unique
    private static Color toColor(Vector4f color) {
        return Color.builder().red(color.x).green(color.y).blue(color.z).alpha(color.w).build();
    }

    @Unique
    private static Fog fogOf(FogCharacteristics fogCharacteristics) {
        return new Fog(
                fogCharacteristics.startDistance(),
                fogCharacteristics.endDistance(),
                SPHERE,
                fogCharacteristics.color().red(),
                fogCharacteristics.color().green(),
                fogCharacteristics.color().blue(),
                fogCharacteristics.color().alpha()
        );
    }
}
