package com.naocraftlab.foggypalegarden.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogCharacteristics;
import com.naocraftlab.foggypalegarden.domain.model.FogMode;
import com.naocraftlab.foggypalegarden.domain.model.Weather;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import lombok.val;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static com.naocraftlab.foggypalegarden.converter.GameTypeConverter.toDomainGameType;
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

    @Shadow
    private static boolean fogEnabled;


    @Inject(method = "computeFogColor", at = @At("RETURN"), cancellable = true)
    private static void injectComputeFogColor(
            Camera camera,
            float partialTick,
            ClientLevel level,
            int renderDistance,
            float darkenWorldAmount,
            CallbackInfoReturnable<Vector4f> cir
    ) {
        val gameFogColor = cir.getReturnValue();
        val newColor = FogService.calculateFogColor(toColor(gameFogColor));
        cir.setReturnValue(toVector4f(newColor));
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void injectSetupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            Vector4f fogColor,
            float renderDistance,
            boolean isFoggy,
            float partialTick,
            CallbackInfoReturnable<FogParameters> cir
    ) {
        final Entity entity = camera.getEntity();
        final GameType gameMode = resolveGameMode(entity);
        final FogType fogType = camera.getFluidInCamera();
        final boolean canApplyFog = fogEnabled
                && !configFacade().isNoFogGameMode(toDomainGameType(gameMode))
                && fogType != FogType.LAVA
                && fogType != FogType.POWDER_SNOW
                && fogType != FogType.WATER
                // level.effects().isFoggyAt(Mth.floor(d), Mth.floor(e)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()
                && !isFoggy
                && !hasMobEffect(entity);

        // fogMode == FogRenderer.FogMode.FOG_SKY || fogMode == FogRenderer.FogMode.FOG_TERRAIN
        val world = (ClientLevel) entity.getCommandSenderWorld();
        val blockPos = camera.getBlockPosition();
        val biomeEntry = world.getBiome(blockPos);
        val hitResult = world.clip(
                new ClipContext(blockPos.getCenter(), blockPos.offset(0, -256, 0).getCenter(), COLLIDER, NONE, entity)
        );

        FogService.changeFogBinding(
                Environment.builder()
                        .dimension(world.dimension().location().toString())
                        .biome(biomeEntry.getRegisteredName())
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

        val characteristics = FogService.calculateFogCharacteristics(FogMode.valueOf(fogMode.name()), renderDistance);
        if (characteristics != null) {
            cir.setReturnValue(fogOf(characteristics, toColor(fogColor)));
            cir.cancel();
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
    private static Vector4f toVector4f(Color color) {
        return new Vector4f(color.red(), color.green(), color.blue(), color.alpha());
    }

    @Unique
    private static FogParameters fogOf(FogCharacteristics fogCharacteristics, Color fogColor) {
        return new FogParameters(
                fogCharacteristics.startDistance(),
                fogCharacteristics.endDistance(),
                FogShape.valueOf(fogCharacteristics.shape().name()),
                fogColor.red(),
                fogColor.green(),
                fogColor.blue(),
                fogColor.alpha()
        );
    }
}
