package com.naocraftlab.foggypalegarden.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static net.minecraft.client.render.FogShape.SPHERE;
import static net.minecraft.world.LightType.SKY;
import static net.minecraft.world.RaycastContext.FluidHandling.NONE;
import static net.minecraft.world.RaycastContext.ShapeType.COLLIDER;

@Mixin(BackgroundRenderer.class)
public abstract class PaleGardenFogMixin {

    private static final Set<String> PALE_GARDEN_BIOME_IDS = Set.of(
            "minecraft:pale_garden"
    );
    private static final float FOG_START = 0.0f;
    private static final float FOG_END = 10.0f;
    private static final float TRANSITION_SPEED = 0.003f;   // (0.0 to 1.0)
    private static final int MIN_FLIGHT_HEIGHT = 15;

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
        final ClientWorld world = (ClientWorld) camera.getFocusedEntity().getWorld();
        final BlockPos blockPos = camera.getBlockPos();
        final RegistryEntry<Biome> biomeEntry = world.getBiome(blockPos);

        final BlockHitResult hitResult = world.raycast(new RaycastContext(
                blockPos.toCenterPos(), blockPos.add(0, -256, 0).toCenterPos(), COLLIDER, NONE, camera.getFocusedEntity()
        ));
        final double relativeHeight = blockPos.getY() - hitResult.getPos().y;

        final boolean playerFlying = relativeHeight > MIN_FLIGHT_HEIGHT;
        final boolean isSupportedBiome = PALE_GARDEN_BIOME_IDS.contains(biomeEntry.getIdAsString());
        final boolean isNotCave = world.getLightLevel(SKY, blockPos) > 4;
        if (isSupportedBiome && isNotCave && !playerFlying) {
            fogDensity = Math.min(fogDensity + TRANSITION_SPEED, 1.0f);
        } else {
            fogDensity = Math.max(fogDensity - TRANSITION_SPEED, 0.0f);
        }

        if (fogDensity > 0.0f) {
            final float brightness = (0.299f * color.x + 0.587f * color.y + 0.114f * color.z) * color.w;
            final Fog fog = new Fog(FOG_START, FOG_END, SPHERE, brightness, brightness, brightness, fogDensity);
            cir.setReturnValue(fog);
            cir.cancel();
        }
    }
}
