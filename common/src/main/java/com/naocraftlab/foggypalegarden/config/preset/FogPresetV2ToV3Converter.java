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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public final class FogPresetV2ToV3Converter implements Converter<FogPreset, FogPreset> {

    @Override
    public @NotNull FogPreset convert(@NotNull FogPreset source) {
        val fogPresetV2 = (FogPresetV2) source;
        return FogPresetV3.builder()
                .code(fogPresetV2.getCode())
                .bindings(
                        fogPresetV2.getBindings().stream()
                                .map(binding -> Binding.builder()
                                        .condition(toRootConditionV3(binding, binding.getCondition()))
                                        .startDistance(binding.startDistance())
                                        .endDistance(binding.endDistance())
                                        .opacity(binding.opacity())
                                        .encapsulationSpeed(binding.encapsulationSpeed())
                                        .brightness(
                                                binding.brightness() != null
                                                        ? Brightness.builder()
                                                        .mode(BrightnessMode.valueOf(binding.brightness().getMode().name()))
                                                        .fixedBrightness(binding.brightness().getFixedBrightness())
                                                        .adjustment(binding.brightness().getAdjustment())
                                                        .build()
                                                        : null
                                        ).color(
                                                binding.color() != null
                                                        ? Color.builder()
                                                        .mode(ColorMode.valueOf(binding.color().getMode().name()))
                                                        .fixedHex(binding.color().getFixedHex())
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
        if (binding.getSkyLightStartLevel() != null) {
            andForRoot.add(
                    Condition.builder()
                            .skyLightLevel(SkyLightLevel.builder().min(binding.getSkyLightStartLevel()).build())
                            .build()
            );
        }
        if (binding.getSurfaceHeightEnd() != null) {
            andForRoot.add(
                    Condition.builder()
                            .surfaceHeight(SurfaceHeight.builder().max(binding.getSurfaceHeightEnd()).build())
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
                .dimensionIn(condition.getDimensionIn())
                .biomeIdIn(condition.getBiomeIdIn())
                .biomeTemperature(toTemperatureV3(condition.getBiomeTemperature()))
                .difficultyIn(condition.getDifficultyIn())
                .weatherIn(toWeathersV3(condition.getWeatherIn()))
                .timeIn(toTimePeriodV3(condition.getTimeIn()))
                .and(condition.getAnd() != null ? condition.getAnd().stream().map(this::toConditionV3).collect(toList()) : null)
                .or(condition.getOr() != null ? condition.getOr().stream().map(this::toConditionV3).collect(toList()) : null)
                .not(condition.getNot() != null ? toConditionV3(condition.getNot()) : null).build();
    }

    @Nullable
    private Temperature toTemperatureV3(@Nullable FogPresetV2.Binding.Condition.Temperature temperature) {
        return temperature != null ? Temperature.builder().min(temperature.getMin()).max(temperature.getMax()).build() : null;
    }

    @Nullable
    private Set<Weather> toWeathersV3(@Nullable Set<FogPresetV2.Binding.Condition.Weather> weathers) {
        return weathers != null ? weathers.stream().map(weather -> Weather.valueOf(weather.name())).collect(toSet()) : null;
    }

    @Nullable
    private TimePeriod toTimePeriodV3(@Nullable FogPresetV2.Binding.Condition.TimePeriod timePeriod) {
        return timePeriod != null ? TimePeriod.builder().start(timePeriod.getStart()).end(timePeriod.getEnd()).build() : null;
    }
}
