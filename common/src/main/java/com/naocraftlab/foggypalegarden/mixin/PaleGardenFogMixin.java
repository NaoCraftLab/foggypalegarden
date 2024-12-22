package com.naocraftlab.foggypalegarden.mixin;

import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogCharacteristics;
import com.naocraftlab.foggypalegarden.domain.model.Weather;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import lombok.val;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.CLEAR;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.RAIN;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.THUNDER;
import static net.minecraft.world.level.ClipContext.Block.COLLIDER;
import static net.minecraft.world.level.ClipContext.Fluid.NONE;
import static net.minecraft.world.level.GameType.ADVENTURE;
import static net.minecraft.world.level.GameType.CREATIVE;
import static net.minecraft.world.level.GameType.SPECTATOR;
import static net.minecraft.world.level.GameType.SURVIVAL;
import static net.minecraft.world.level.LightLayer.SKY;

@Mixin(FogRenderer.class)
public abstract class PaleGardenFogMixin {

    @Unique
    private static float fogDensity = 0.0f;

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void injectSetupFog(
            Camera camera,
            FogRenderer.FogMode fogType,
            Vector4f color,
            float viewDistance,
            boolean thickenFog,
            float tickDelta,
            CallbackInfoReturnable<FogParameters> cir
    ) {
        val focusedEntity = camera.getEntity();

        val gameMode = resolveGameMode(focusedEntity);
        if (configFacade().isNoFogGameMode(gameMode)) {
            fogDensity = 0.0f;
            return;
        }
        val world = (ClientLevel) focusedEntity.getCommandSenderWorld();
        val blockPos = camera.getBlockPosition();
        val biomeEntry = world.getBiome(blockPos);
        val hitResult = world.clip(new ClipContext(
                blockPos.getCenter(), blockPos.offset(0, -256, 0).getCenter(), COLLIDER, NONE, focusedEntity
        ));

        val fogCharacteristics = FogService.calculateFogCharacteristics(
                Environment.builder()
                        .dimension(world.dimension().location().toString())
                        .biome(biomeEntry.getRegisteredName())
                        .biomeTemperature(biomeEntry.value().getBaseTemperature())
                        .difficulty(world.getDifficulty())
                        .weather(resolveWeather(world))
                        .timeOfDay(world.getDayTime())
                        .skyLightLevel(world.getBrightness(SKY, blockPos))
                        .height(blockPos.getY())
                        .heightAboveSurface(blockPos.getY() - hitResult.getBlockPos().getY())
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

            color.x = color.x * (1.0f - fogDensity) + fogCharacteristics.color().red() * fogDensity;
            color.y = color.y * (1.0f - fogDensity) + fogCharacteristics.color().green() * fogDensity;
            color.z = color.z * (1.0f - fogDensity) + fogCharacteristics.color().blue() * fogDensity;
            color.w = color.w * (1.0f - fogDensity) + fogCharacteristics.color().alpha() * fogDensity;

            cir.setReturnValue(fogOf(fogCharacteristics));
            cir.cancel();
        }
    }

    @Unique
    private static GameType resolveGameMode(Entity player) {
        final Abilities attributes;
        if (player instanceof LocalPlayer) {
            attributes = ((LocalPlayer) player).getAbilities();
        } else {
            return SPECTATOR;
        }
        if (attributes.instabuild) {
            return CREATIVE;
        } else if (attributes.mayfly && attributes.invulnerable && attributes.flying) {
            return SPECTATOR;
        } else if (attributes.mayBuild) {
            return SURVIVAL;
        }
        return ADVENTURE;
    }

    @Unique
    private static Weather resolveWeather(ClientLevel world) {
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
    private static FogParameters fogOf(FogCharacteristics fogCharacteristics) {
        return new FogParameters(
                fogCharacteristics.startDistance(),
                fogCharacteristics.endDistance(),
                fogCharacteristics.shape(),
                fogCharacteristics.color().red(),
                fogCharacteristics.color().green(),
                fogCharacteristics.color().blue(),
                fogCharacteristics.color().alpha()
        );
    }
}
