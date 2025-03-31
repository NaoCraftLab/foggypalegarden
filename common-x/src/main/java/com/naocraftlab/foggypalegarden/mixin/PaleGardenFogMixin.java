package com.naocraftlab.foggypalegarden.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogMode;
import com.naocraftlab.foggypalegarden.domain.model.GameType;
import com.naocraftlab.foggypalegarden.domain.model.Weather;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import lombok.val;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.FogType;
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

    @Unique
    private static FogPresetV3.Binding binding = null;

    @Unique
    private static float fogDensity = 0.0f;

    @Unique
    private static boolean hasMobEffect(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.hasEffect(MobEffects.BLINDNESS) || livingEntity.hasEffect(MobEffects.DARKNESS);
        } else {
            return false;
        }
    }

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void injectSetupColor(
            Camera camera,
            float f,
            ClientLevel clientLevel,
            int i,
            float g,
            CallbackInfo ci
    ) {
        val color = Color.builder().red(fogRed).green(fogGreen).blue(fogBlue).alpha(1.0f).build();
        val currentColor = FogService.calculateColor(binding, fogDensity, color);
        fogRed = currentColor.red();
        fogGreen = currentColor.green();
        fogBlue = currentColor.blue();
        RenderSystem.clearColor(currentColor.red(), currentColor.green(), currentColor.blue(), 1.0f);
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void injectSetupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float viewDistance,
            boolean thickenFog,
            float tickDelta,
            CallbackInfo ci
    ) {
        val fogType = camera.getFluidInCamera();
        val entity = camera.getEntity();
        val gameMode = resolveGameMode(entity);
        if (configFacade().isNoFogGameMode(gameMode) || fogType != FogType.NONE) {
            return;
        }
        if (hasMobEffect(entity)) {
            fogDensity = 0.0f;
            return;
        }
        val world = (ClientLevel) entity.getCommandSenderWorld();
        val blockPos = camera.getBlockPosition();
        val biomeEntry = world.getBiome(blockPos);
        val hitResult = world.clip(new ClipContext(
                blockPos.getCenter(), blockPos.offset(0, -256, 0).getCenter(), COLLIDER, NONE, entity
        ));

        val currentColor = Color.builder().red(fogRed).green(fogGreen).blue(fogBlue).alpha(1.0f).build();
        binding = FogService.resolveBinding(
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
                        .gameFogColor(currentColor)
                        .fogDensity(fogDensity)
                        .build()
        );
        val fogCharacteristics = FogService.calculateFogCharacteristics(
                binding,
                fogDensity,
                FogMode.valueOf(fogMode.name()),
                viewDistance,
                currentColor
        );

        assert fogCharacteristics.fogDensity() >= 0.0f : "FPG: Fog density is negative";
        assert fogCharacteristics.fogDensity() <= 1.0f : "FPG: Fog density is greater than 1.0";

        fogDensity = fogCharacteristics.fogDensity();
        if (fogDensity > 0.0f) {
            assert fogCharacteristics.startDistance() >= 0.0f : "FPG: Start distance is negative";
            assert fogCharacteristics.endDistance() >= fogCharacteristics.startDistance()
                    : "FPG: End distance is less than start distance";

            RenderSystem.setShaderFogStart(fogCharacteristics.startDistance());
            RenderSystem.setShaderFogEnd(fogCharacteristics.endDistance());
            RenderSystem.setShaderFogShape(FogShape.valueOf(fogCharacteristics.shape().name()));

            ci.cancel();
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
