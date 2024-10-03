package com.naocraftlab.foggypalegarden.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.client.render.FogShape.SPHERE;

@Mixin(BackgroundRenderer.class)
public abstract class PaleGardenFogMixin {

    private static final Identifier PALE_GARDEN_BIOME_ID = Identifier.of("minecraft", "pale_garden");
    private static final float TRANSITION_SPEED = 0.006f;   // (0.0 to 1.0)

    private static float fogDensity = 0.0f;

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void injectApplyFog(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            Vector4f color,
            float viewDistance,
            boolean thickenFog,
            float tickDelta,
            CallbackInfoReturnable<Fog> cir
    ) {
        final ClientWorld world = (ClientWorld) camera.getFocusedEntity().getWorld();
        final BlockPos cameraPos = camera.getBlockPos();
        final RegistryEntry<Biome> biomeEntry = world.getBiome(cameraPos);
        boolean currentlyInPaleGarden = biomeEntry.matchesId(PALE_GARDEN_BIOME_ID);
        if (currentlyInPaleGarden) {
            fogDensity = Math.min(fogDensity + TRANSITION_SPEED, 1.0f);
        } else {
            fogDensity = Math.max(fogDensity - TRANSITION_SPEED, 0.0f);
        }

        if (fogDensity > 0.0f) {
            float fogStart = 0.0f;
            float fogEnd = (1.0f - fogDensity) * viewDistance + fogDensity * 10.0f;
            final float brightness = (0.299f * color.x + 0.587f * color.y + 0.114f * color.z) * color.w;
            final Fog fog = new Fog(fogStart, fogEnd, SPHERE, brightness, brightness, brightness, 1f);
            cir.setReturnValue(fog);
            cir.cancel();
        }
    }
}
