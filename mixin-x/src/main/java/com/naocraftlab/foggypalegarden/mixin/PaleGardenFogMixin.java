package com.naocraftlab.foggypalegarden.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogCharacteristics;
import com.naocraftlab.foggypalegarden.domain.model.FogMode;
import com.naocraftlab.foggypalegarden.domain.model.GameType;
import com.naocraftlab.foggypalegarden.domain.model.Weather;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.CLEAR;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.RAIN;
import static com.naocraftlab.foggypalegarden.domain.model.Weather.THUNDER;
import static net.minecraft.world.level.ClipContext.Block.COLLIDER;
import static net.minecraft.world.level.ClipContext.Fluid.NONE;
import static net.minecraft.world.level.LightLayer.SKY;

@Mixin(FogRenderer.class)
public abstract class PaleGardenFogMixin {

    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;


    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void injectSetupColor(
            Camera activeRenderInfo,
            float partialTicks,
            ClientLevel level,
            int renderDistanceChunks,
            float bossColorModifier,
            CallbackInfo ci
    ) {
        final Color gameFogColor = Color.builder().red(fogRed).green(fogGreen).blue(fogBlue).alpha(1.0f).build();
        final Color newColor = FogService.calculateFogColor(gameFogColor);
        fogRed = newColor.red();
        fogGreen = newColor.green();
        fogBlue = newColor.blue();
        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 1.0f);
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void injectSetupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float farPlaneDistance,
            boolean isFoggy,
            float f,
            CallbackInfo ci
    ) {
        final Entity entity = camera.getEntity();
        final GameType gameMode = resolveGameMode(entity);
        final FogType fogType = camera.getFluidInCamera();
        final boolean canApplyFog = !configFacade().isNoFogGameMode(gameMode)
                && fogType != FogType.LAVA
                && fogType != FogType.POWDER_SNOW
                && fogType != FogType.WATER
                // level.effects().isFoggyAt(Mth.floor(d), Mth.floor(e)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()
                && !isFoggy
                && !hasMobEffect(entity);

        // fogMode == FogRenderer.FogMode.FOG_SKY || fogMode == FogRenderer.FogMode.FOG_TERRAIN
        final ClientLevel world = (ClientLevel) entity.getCommandSenderWorld();
        final BlockPos blockPos = camera.getBlockPosition();
        final Holder<Biome> biomeEntry = world.getBiome(blockPos);
        final BlockHitResult hitResult = world.clip(
                new ClipContext(blockPos.getCenter(), blockPos.offset(0, -256, 0).getCenter(), COLLIDER, NONE, entity)
        );

        FogService.changeFogBinding(
                Environment.builder()
                        .dimension(world.dimension().location().toString())
                        .biome(biomeEntry.unwrapKey().get().location().toString())
                        .biomeTemperature(biomeEntry.value().getBaseTemperature())
                        .difficulty(world.getDifficulty())
                        .weather(resolveWeather(world))
                        .timeOfDay(world.getDayTime() % 24000)
                        .skyLightLevel(world.getBrightness(SKY, blockPos))
                        .height(blockPos.getY())
                        .heightAboveSurface(blockPos.getY() - hitResult.getBlockPos().getY())
                        .canApplyFog(canApplyFog)
                        .build()
        );

        final float originalFogStart = (fogMode == FogRenderer.FogMode.FOG_TERRAIN)
                ? farPlaneDistance - Mth.clamp(farPlaneDistance / 10.0F, 4.0F, 64.0F)
                : 0.0f;
        final FogCharacteristics characteristics = FogService.calculateFogCharacteristics(
                FogMode.valueOf(fogMode.name()),
                originalFogStart,
                farPlaneDistance
        );
        if (characteristics != null) {
            RenderSystem.setShaderFogStart(characteristics.startDistance());
            RenderSystem.setShaderFogEnd(characteristics.endDistance());
            RenderSystem.setShaderFogShape(FogShape.valueOf(characteristics.shape().name()));
            ci.cancel();
        }
    }


    @Unique
    private static boolean hasMobEffect(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.hasEffect(MobEffects.BLINDNESS) || livingEntity.hasEffect(MobEffects.DARKNESS);
        } else {
            return false;
        }
    }

    @Unique
    private static GameType resolveGameMode(Entity player) {
        final Abilities attributes;
        if (player instanceof LocalPlayer) {
            attributes = ((LocalPlayer) player).getAbilities();
        } else {
            return GameType.SPECTATOR;
        }
        if (attributes.instabuild) {
            return GameType.CREATIVE;
        } else if (attributes.mayfly && attributes.invulnerable && attributes.flying) {
            return GameType.SPECTATOR;
        } else if (attributes.mayBuild) {
            return GameType.SURVIVAL;
        }
        return GameType.ADVENTURE;
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
}
