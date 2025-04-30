package com.naocraftlab.foggypalegarden.config.presetsource;

import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.SkyLightLevel;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.SurfaceHeight;
import com.naocraftlab.foggypalegarden.domain.model.FogShape;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.naocraftlab.foggypalegarden.config.presetsource.PresetSource.PresetSourceType.EMBEDDED;
import static java.util.stream.Collectors.toList;
import static net.minecraft.world.Difficulty.EASY;
import static net.minecraft.world.Difficulty.HARD;
import static net.minecraft.world.Difficulty.NORMAL;
import static net.minecraft.world.Difficulty.PEACEFUL;

public final class PresetSourceEmbedded implements PresetSource<FogPresetV3> {

    public static final String DEFAULT_PRESET_CODE = "FPG_STEPHEN_KING";

    private static final List<FogPresetV3> DEFAULT_PRESETS = List.of(
            FogPresetV3.builder()
                    .code("FPG_AMBIANCE")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().biomeIdIn(Set.of("*:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(32.0f)
                                    .opacity(95.0f)
                                    .encapsulationSpeed(1.5f)
                                    .brightness(Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .shape(FogShape.SPHERE)
                                    .build()
                    )).build(),
            FogPresetV3.builder()
                    .code("FPG_I_AM_NOT_AFRAID_BUT")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().biomeIdIn(Set.of("*:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(16.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(1.5f)
                                    .brightness(Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .shape(FogShape.SPHERE)
                                    .build()
                    )).build(),
            FogPresetV3.builder()
                    .code(DEFAULT_PRESET_CODE)
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().biomeIdIn(Set.of("*:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(0.0f)
                                    .endDistance(10.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(1.5f)
                                    .brightness(Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .shape(FogShape.SPHERE)
                                    .build()
                    )).build(),
            FogPresetV3.builder()
                    .code("FPG_DIFFICULTY_BASED")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(Set.of(PEACEFUL, EASY)).build(),
                                                    Condition.builder().biomeIdIn(Set.of("*:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(32.0f)
                                    .opacity(95.0f)
                                    .encapsulationSpeed(1.5f)
                                    .brightness(Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .shape(FogShape.SPHERE)
                                    .build(),
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(Set.of(NORMAL)).build(),
                                                    Condition.builder().biomeIdIn(Set.of("*:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(16.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(1.5f)
                                    .brightness(Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .shape(FogShape.SPHERE)
                                    .build(),
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(Set.of(HARD)).build(),
                                                    Condition.builder().biomeIdIn(Set.of("*:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(0.0f)
                                    .endDistance(10.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(1.5f)
                                    .brightness(Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .shape(FogShape.SPHERE)
                                    .build()
                    )).build()
    );

    @Override
    public PresetSourceType type() {
        return EMBEDDED;
    }

    @Override
    public List<PresetBox<FogPresetV3>> load() {
        return DEFAULT_PRESETS.stream().map(preset ->
                PresetBox.<FogPresetV3>builder()
                        .sourceType(type())
                        .code(preset.getCode())
                        .path(preset.getCode() + ".json")
                        .preset(preset)
                        .build()
        ).collect(toList());
    }

    @Override
    public void save(Collection<PresetBox<FogPresetV3>> presets) {
        // not supported
    }
}
