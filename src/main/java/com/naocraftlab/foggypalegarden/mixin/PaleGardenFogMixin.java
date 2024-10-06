package com.naocraftlab.foggypalegarden.mixin;

import com.naocraftlab.foggypalegarden.config.ModConfigV1;
import com.naocraftlab.foggypalegarden.config.ModConfigV1.FogSettings;
import com.naocraftlab.foggypalegarden.util.FoggyPaleGardenException;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.naocraftlab.foggypalegarden.config.ConfigManager.CONFIG_PATH;
import static com.naocraftlab.foggypalegarden.config.ConfigManager.FOG_PRESETS;
import static com.naocraftlab.foggypalegarden.config.ConfigManager.currentConfig;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.AMBIANCE;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.CUSTOM;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.DIFFICULTY_BASED;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.I_AM_NOT_AFRAID_BUT;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.STEPHEN_KING;
import static net.minecraft.client.render.FogShape.SPHERE;
import static net.minecraft.world.Difficulty.EASY;
import static net.minecraft.world.Difficulty.NORMAL;
import static net.minecraft.world.Difficulty.PEACEFUL;
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
        final ClientWorld world = (ClientWorld) camera.getFocusedEntity().getWorld();
        final ModConfigV1 config = currentConfig();
        final FogSettings fogSettings = resolveFogSettings(config, world.getDifficulty());

        final BlockPos blockPos = camera.getBlockPos();
        final RegistryEntry<Biome> biomeEntry = world.getBiome(blockPos);
        final BlockHitResult hitResult = world.raycast(new RaycastContext(
                blockPos.toCenterPos(), blockPos.add(0, -256, 0).toCenterPos(), COLLIDER, NONE, camera.getFocusedEntity()
        ));
        final double relativeHeight = blockPos.getY() - hitResult.getPos().y;

        final boolean playerFlying = relativeHeight > fogSettings.getSurfaceHeightEnd();
        final boolean isSupportedBiome = config.getBiomes().contains(biomeEntry.getIdAsString());
        final boolean isNotCave = world.getLightLevel(SKY, blockPos) > fogSettings.getSkyLightStartLevel();
        if (isSupportedBiome && isNotCave && !playerFlying) {
            fogDensity = Math.min(fogDensity + fogSettings.getEncapsulationSpeed() / 100f / 20f, 1.0f);
        } else {
            fogDensity = Math.max(fogDensity - fogSettings.getEncapsulationSpeed() / 100f / 20f, 0.0f);
        }

        if (fogDensity > 0.0f) {
            final Fog fog = createFog(fogSettings, color);
            cir.setReturnValue(fog);
            cir.cancel();
        }
    }

    @Unique
    private static Fog createFog(FogSettings fogSettings, Vector4f color) {
        final float brightness = (0.299f * color.x + 0.587f * color.y + 0.114f * color.z) * color.w;
        final float alpha = (fogSettings.getOpacity() > 0f) ? fogDensity * ((fogSettings.getOpacity() - 0.001f) / 100f) : 0f;
        final Fog fog = new Fog(
                fogSettings.getStartDistance(),
                fogSettings.getEndDistance(),
                SPHERE,
                brightness,
                brightness,
                brightness,
                alpha
        );
        return fog;
    }

    @Unique
    private static FogSettings resolveFogSettings(ModConfigV1 config, Difficulty difficulty) {
        final FogSettings fogSettings;
        if (config.getFogPreset() == DIFFICULTY_BASED) {
            if (difficulty == PEACEFUL || difficulty == EASY) {
                fogSettings = FOG_PRESETS.get(AMBIANCE);
            } else if (difficulty == NORMAL) {
                fogSettings = FOG_PRESETS.get(I_AM_NOT_AFRAID_BUT);
            } else {
                fogSettings = FOG_PRESETS.get(STEPHEN_KING);
            }
        } else if (config.getFogPreset() == CUSTOM) {
            fogSettings = config.getCustomFog();
        } else {
            fogSettings = FOG_PRESETS.get(config.getFogPreset());
        }
        if (fogSettings == null) {
            throw new FoggyPaleGardenException("Incorrect fogPreset value in config file (" + CONFIG_PATH.toAbsolutePath() + ")");
        }
        return fogSettings;
    }
}
