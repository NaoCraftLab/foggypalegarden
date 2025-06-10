package com.naocraftlab.foggypalegarden.mixin;

import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogCharacteristics;
import com.naocraftlab.foggypalegarden.domain.model.FogMode;
import com.naocraftlab.foggypalegarden.domain.model.Weather;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AirBasedFogEnvironment;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentFpgMixin extends AirBasedFogEnvironment {

    @Override
    public int getBaseColor(ClientLevel clientLevel, Camera camera, int i, float f) {
        final int gameFogColor = super.getBaseColor(clientLevel, camera, i, f);
        final Color newColor = FogService.calculateFogColor(
                Color.builder()
                        .red(ARGB.red(gameFogColor) / 255.0F)
                        .green(ARGB.green(gameFogColor) / 255.0F)
                        .blue(ARGB.blue(gameFogColor) / 255.0F)
                        .alpha(ARGB.alpha(gameFogColor) / 255.0F)
                        .build()
        );
        return ARGB.colorFromFloat(newColor.alpha(), newColor.red(), newColor.green(), newColor.blue());
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void injectSetupFog(
            FogData fogData,
            Entity entity,
            BlockPos blockPos,
            ClientLevel clientLevel,
            float renderDistance,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        final GameType gameMode = resolveGameMode(entity);
        final boolean canApplyFog = !configFacade().isNoFogGameMode(toDomainGameType(gameMode));
        final Holder<Biome> biomeEntry = clientLevel.getBiome(blockPos);
        final BlockHitResult hitResult = clientLevel.clip(
                new ClipContext(blockPos.getCenter(), blockPos.offset(0, -256, 0).getCenter(), COLLIDER, NONE, entity)
        );

        FogService.changeFogBinding(
                Environment.builder()
                        .dimension(clientLevel.dimension().location().toString())
                        .biome(biomeEntry.getRegisteredName())
                        .biomeTemperature(biomeEntry.value().getBaseTemperature())
                        .difficulty(clientLevel.getDifficulty())
                        .weather(resolveWeather(clientLevel))
                        .timeOfDay(clientLevel.getDayTime() % 24000)
                        .skyLightLevel(clientLevel.getBrightness(SKY, blockPos))
                        .height(blockPos.getY())
                        .heightAboveSurface(blockPos.getY() - hitResult.getBlockPos().getY())
                        .canApplyFog(canApplyFog)
                        .build()
        );

        final FogCharacteristics terrainCharacteristics = FogService.calculateFogCharacteristics(FogMode.FOG_TERRAIN, renderDistance);
        final FogCharacteristics skyCharacteristics = FogService.calculateFogCharacteristics(FogMode.FOG_SKY, renderDistance);
        if (terrainCharacteristics != null && skyCharacteristics != null) {
            fogData.environmentalStart = terrainCharacteristics.getStartDistance();
            fogData.environmentalEnd = terrainCharacteristics.getEndDistance();
            fogData.skyEnd = skyCharacteristics.endDistance();
            fogData.cloudEnd = (float) (Minecraft.getInstance().options.cloudRange().get() * 16);
            ci.cancel();
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
}
