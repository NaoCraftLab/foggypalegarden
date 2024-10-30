package com.naocraftlab.foggypalegarden.config.preset;

import com.naocraftlab.foggypalegarden.config.main.MainConfigV1;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding.Brightness;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding.Color;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding.Condition;
import com.naocraftlab.foggypalegarden.util.Converter;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.naocraftlab.foggypalegarden.util.FpgCollections.treeSetOf;
import static net.minecraft.world.Difficulty.EASY;
import static net.minecraft.world.Difficulty.HARD;
import static net.minecraft.world.Difficulty.NORMAL;
import static net.minecraft.world.Difficulty.PEACEFUL;

@RequiredArgsConstructor
public class MainConfigV1ToFogPresetsV2Converter implements Converter<MainConfigV1, Map<Path, FogPresetV2>> {

    private static final List<FogPresetV2> DEFAULT_PRESETS = List.of(
            FogPresetV2.builder()
                    .code("FPG_AMBIANCE")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                    .startDistance(2.0f)
                                    .skyLightStartLevel(4)
                                    .endDistance(15.0f)
                                    .surfaceHeightEnd(15.0f)
                                    .opacity(95.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build(),
            FogPresetV2.builder()
                    .code("FPG_I_AM_NOT_AFRAID_BUT")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                    .startDistance(2.0f)
                                    .skyLightStartLevel(4)
                                    .endDistance(15.0f)
                                    .surfaceHeightEnd(15.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build(),
            FogPresetV2.builder()
                    .code("FPG_STEPHEN_KING")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                    .startDistance(0.0f)
                                    .skyLightStartLevel(4)
                                    .endDistance(10.0f)
                                    .surfaceHeightEnd(15.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build(),
            FogPresetV2.builder()
                    .code("FPG_DIFFICULTY_BASED")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(treeSetOf(PEACEFUL, EASY)).build(),
                                                    Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .skyLightStartLevel(4)
                                    .endDistance(15.0f)
                                    .surfaceHeightEnd(15.0f)
                                    .opacity(95.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build(),
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(treeSetOf(NORMAL)).build(),
                                                    Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .skyLightStartLevel(4)
                                    .endDistance(15.0f)
                                    .surfaceHeightEnd(15.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build(),
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(treeSetOf(HARD)).build(),
                                                    Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                            )).build()
                                    ).startDistance(0.0f)
                                    .skyLightStartLevel(4)
                                    .endDistance(10.0f)
                                    .surfaceHeightEnd(15.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build()
    );

    private final Path presetDirectoryPath;

    @Override
    public @NotNull Map<Path, FogPresetV2> convert(@NotNull MainConfigV1 mainConfig) {
        val presets = new ArrayList<>(DEFAULT_PRESETS);
        if (mainConfig.getFogPreset() == MainConfigV1.FogPreset.CUSTOM) {
            presets.add(
                    FogPresetV2.builder()
                            .code("CUSTOM")
                            .bindings(List.of(
                                    Binding.builder()
                                            .condition(Condition.builder().biomeIdIn(mainConfig.getBiomes()).build())
                                            .startDistance(mainConfig.getCustomFog().startDistance())
                                            .skyLightStartLevel(mainConfig.getCustomFog().skyLightStartLevel())
                                            .endDistance(mainConfig.getCustomFog().endDistance())
                                            .surfaceHeightEnd(mainConfig.getCustomFog().surfaceHeightEnd())
                                            .opacity(mainConfig.getCustomFog().opacity())
                                            .encapsulationSpeed(mainConfig.getCustomFog().encapsulationSpeed())
                                            .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
        }
        return presets.stream()
                .map(preset -> new Pair<>(
                        presetDirectoryPath.resolve(preset.getCode() + ".json"),
                        preset
                )).collect(Collectors.toMap(Pair::first, Pair::second));
    }
}
