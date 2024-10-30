package com.naocraftlab.foggypalegarden.config.preset;

import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Brightness;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Color;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.SkyLightLevel;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.SurfaceHeight;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.Temperature;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.TimePeriod;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.Weather;
import com.naocraftlab.foggypalegarden.util.Converter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class FogPresetV2ToV3Converter implements Converter<FogPreset, FogPreset> {

    @Override
    public @NotNull FogPreset convert(@NotNull FogPreset source) {
        val fogPresetV2 = (FogPresetV2) source;
        return FogPresetV3.builder()
                .code(fogPresetV2.getCode())
                .bindings(
                        fogPresetV2.getBindings().stream()
                                .map(binding -> Binding.builder()
                                        .condition(toRootConditionV3(binding, binding.condition()))
                                        .startDistance(binding.startDistance())
                                        .endDistance(binding.endDistance())
                                        .opacity(binding.opacity())
                                        .encapsulationSpeed(binding.encapsulationSpeed())
                                        .brightness(
                                                binding.brightness() != null
                                                        ? Brightness.builder()
                                                        .mode(BrightnessMode.valueOf(binding.brightness().mode().name()))
                                                        .fixedBrightness(binding.brightness().fixedBrightness())
                                                        .adjustment(binding.brightness().adjustment())
                                                        .build()
                                                        : null
                                        ).color(
                                                binding.color() != null
                                                        ? Color.builder()
                                                        .mode(ColorMode.valueOf(binding.color().mode().name()))
                                                        .fixedHex(binding.color().fixedHex())
                                                        .build()
                                                        : null
                                        ).shape(binding.shape())
                                        .build()
                                ).toList()
                ).build();
    }

    @NotNull
    private Condition toRootConditionV3(@NotNull FogPresetV2.Binding binding, @NotNull FogPresetV2.Binding.Condition condition) {
        val andForRoot = new ArrayList<Condition>();
        if (binding.skyLightStartLevel() != null) {
            andForRoot.add(
                    Condition.builder()
                            .skyLightLevel(SkyLightLevel.builder().min(binding.skyLightStartLevel()).build())
                            .build()
            );
        }
        if (binding.surfaceHeightEnd() != null) {
            andForRoot.add(
                    Condition.builder()
                            .surfaceHeight(SurfaceHeight.builder().max(binding.surfaceHeightEnd()).build())
                            .build());
        }
        if (!andForRoot.isEmpty()) {
            andForRoot.add(toConditionV3(condition));
            return Condition.builder().and(andForRoot).build();
        }
        return toConditionV3(condition);
    }

    @NotNull
    private Condition toConditionV3(@NotNull FogPresetV2.Binding.Condition condition) {
        return Condition.builder()
                .dimensionIn(condition.dimensionIn())
                .biomeIdIn(condition.biomeIdIn())
                .biomeTemperature(toTemperatureV3(condition.biomeTemperature()))
                .difficultyIn(condition.difficultyIn())
                .weatherIn(toWeathersV3(condition.weatherIn()))
                .timeIn(toTimePeriodV3(condition.timeIn()))
                .and(condition.and() != null ? condition.and().stream().map(this::toConditionV3).toList() : null)
                .or(condition.or() != null ? condition.or().stream().map(this::toConditionV3).toList() : null)
                .not(condition.not() != null ? toConditionV3(condition.not()) : null).build();
    }

    @Nullable
    private Temperature toTemperatureV3(@Nullable FogPresetV2.Binding.Condition.Temperature temperature) {
        return temperature != null ? Temperature.builder().min(temperature.min()).max(temperature.max()).build() : null;
    }

    @Nullable
    private Set<Weather> toWeathersV3(@Nullable Set<FogPresetV2.Binding.Condition.Weather> weathers) {
        return weathers != null ? weathers.stream().map(weather -> Weather.valueOf(weather.name())).collect(toSet()) : null;
    }

    @Nullable
    private TimePeriod toTimePeriodV3(@Nullable FogPresetV2.Binding.Condition.TimePeriod timePeriod) {
        return timePeriod != null ? TimePeriod.builder().start(timePeriod.start()).end(timePeriod.end()).build() : null;
    }
}
