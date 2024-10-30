package com.naocraftlab.foggypalegarden.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naocraftlab.foggypalegarden.config.main.MainConfig;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV1;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV1ToV2Converter;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV2;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV2ToV3Converter;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPreset;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2ToV3Converter;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Brightness;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Color;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.SkyLightLevel;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Condition.SurfaceHeight;
import com.naocraftlab.foggypalegarden.config.preset.MainConfigV1ToFogPresetsV2Converter;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenConfigurationException;
import com.naocraftlab.foggypalegarden.util.Converter;
import com.naocraftlab.foggypalegarden.util.FpgFiles;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.Builder;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toMap;
import static net.minecraft.world.Difficulty.EASY;
import static net.minecraft.world.Difficulty.HARD;
import static net.minecraft.world.Difficulty.NORMAL;
import static net.minecraft.world.Difficulty.PEACEFUL;

public final class ConfigMigrator {

    private static final MainConfigV3 DEFAULT_CONFIG = MainConfigV3.builder().preset("FPG_STEPHEN_KING").build();
    private static final List<FogPresetV3> DEFAULT_PRESETS = List.of(
            FogPresetV3.builder()
                    .code("FPG_AMBIANCE")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(15.0f)
                                    .opacity(95.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build(),
            FogPresetV3.builder()
                    .code("FPG_I_AM_NOT_AFRAID_BUT")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(15.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build(),
            FogPresetV3.builder()
                    .code("FPG_STEPHEN_KING")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(0.0f)
                                    .endDistance(10.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build(),
            FogPresetV3.builder()
                    .code("FPG_DIFFICULTY_BASED")
                    .bindings(List.of(
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(Set.of(PEACEFUL, EASY)).build(),
                                                    Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(15.0f)
                                    .opacity(95.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build(),
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(Set.of(NORMAL)).build(),
                                                    Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(2.0f)
                                    .endDistance(15.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build(),
                            Binding.builder()
                                    .condition(
                                            Condition.builder().and(List.of(
                                                    Condition.builder().difficultyIn(Set.of(HARD)).build(),
                                                    Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build(),
                                                    Condition.builder().skyLightLevel(SkyLightLevel.builder().min(4).build()).build(),
                                                    Condition.builder().surfaceHeight(SurfaceHeight.builder().max(15f).build()).build()
                                            )).build()
                                    ).startDistance(0.0f)
                                    .endDistance(10.0f)
                                    .opacity(100.0f)
                                    .encapsulationSpeed(6.0f)
                                    .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                    .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                    .build()
                    )).build()
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<Integer, Converter<MainConfig, MainConfig>> mainConfigConverters;
    private final Map<Integer, Converter<FogPreset, FogPreset>> fogPresetsConverters;

    private final Converter<MainConfigV1, Map<Path, FogPresetV2>> mainConfigV1ToFogPresetsV2Converter;

    public ConfigMigrator(Path presetDirectoryPath) {
        mainConfigConverters = Map.of(
                1, new MainConfigV1ToV2Converter(),
                2, new MainConfigV2ToV3Converter(),
                3, new Converter<>() {

                    @Override
                    public @NotNull MainConfig convert(@NotNull MainConfig source) {
                        return source;
                    }
                }
        );
        fogPresetsConverters = Map.of(
                2, new FogPresetV2ToV3Converter(),
                3, new Converter<>() {

                    @Override
                    public @NotNull FogPreset convert(@NotNull FogPreset source) {
                        return source;
                    }
                }
        );
        mainConfigV1ToFogPresetsV2Converter = new MainConfigV1ToFogPresetsV2Converter(presetDirectoryPath);
    }

    public @NotNull MigrationResult<MainConfigV3, FogPresetV3> migrate(
            @NotNull Path configPath,
            @NotNull Path presetDirectoryPath,
            @Nullable MainConfig mainConfig,
            @NotNull Map<Path, FogPreset> sourcePresetByPath
    ) {
        val migratedConfig = mainConfig != null ? migrateMainConfig(configPath, mainConfig) : DEFAULT_CONFIG;
        val presetByPath = new HashMap<>(sourcePresetByPath);
        final Map<Path, FogPresetV3> migratedPresetByPath;
        if (!presetByPath.isEmpty()) {
            if (mainConfig.getVersion() == 1) {
                val mainConfigV1 = GSON.fromJson(FpgFiles.readString(configPath), MainConfigV1.class);
                val generatedPresetsV2 = mainConfigV1ToFogPresetsV2Converter.convert(mainConfigV1);
                for (val generatedPresetV2Entry : generatedPresetsV2.entrySet()) {
                    FpgFiles.writeString(generatedPresetV2Entry.getKey(), GSON.toJson(generatedPresetV2Entry.getValue()));
                }
                presetByPath.putAll(generatedPresetsV2);
            }
            migratedPresetByPath = migratePresets(presetByPath);
        } else {
            migratedPresetByPath = DEFAULT_PRESETS.stream()
                    .map(preset -> new Pair<>(presetDirectoryPath.resolve(preset.getCode() + ".json"), (FogPreset) preset))
                    .collect(toMap(Pair::first, pair -> (FogPresetV3) pair.second()));
        }
        return MigrationResult.<MainConfigV3, FogPresetV3>builder()
                .configPath(configPath)
                .mainConfig(migratedConfig)
                .presetByPath(migratedPresetByPath)
                .build();
    }

    private @Nullable MainConfigV3 migrateMainConfig(@NotNull Path configPath, @NotNull MainConfig mainConfig) {
        if (!exists(configPath)) {
            return null;
        }
        MainConfig migratedConfig = readMainConfig(configPath, mainConfig.getVersion());
        while (!(migratedConfig instanceof MainConfigV3)) {
            migratedConfig = mainConfigConverters.get(migratedConfig.getVersion()).convert(migratedConfig);
        }
        return (MainConfigV3) migratedConfig;
    }

    private @NotNull Map<Path, FogPresetV3> migratePresets(@NotNull Map<Path, FogPreset> presetByPath) {
        Map<Path, FogPreset> migratedFogPresetByPath = presetByPath.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), readFogPreset(entry.getKey(), entry.getValue().getVersion())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        while (migratedFogPresetByPath.values().stream().anyMatch(preset -> !(preset instanceof FogPresetV3))) {
            migratedFogPresetByPath = migratedFogPresetByPath.entrySet().stream()
                    .map(entry -> Map.entry(
                            entry.getKey(),
                            fogPresetsConverters.get(entry.getValue().getVersion()).convert(entry.getValue())
                    )).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return migratedFogPresetByPath.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), (FogPresetV3) entry.getValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private @NotNull MainConfig readMainConfig(@NotNull Path configPath, int version) {
        val json = FpgFiles.readString(configPath);
        if (version == 1) {
            return GSON.fromJson(json, MainConfigV1.class);
        } else if (version == 2) {
            return GSON.fromJson(json, MainConfigV2.class);
        } else if (version == 3) {
            return GSON.fromJson(json, MainConfigV3.class);
        } else {
            throw new FoggyPaleGardenConfigurationException("Unsupported main config version: " + version);
        }
    }

    private @NotNull FogPreset readFogPreset(@NotNull Path presetPath, int version) {
        val json = FpgFiles.readString(presetPath);
        if (version == 2) {
            return GSON.fromJson(json, FogPresetV2.class);
        } else if (version == 3) {
            return GSON.fromJson(json, FogPresetV3.class);
        } else {
            throw new FoggyPaleGardenConfigurationException("Unsupported fog preset version: " + version);
        }
    }

    @Builder
    public record MigrationResult<C, P>(
            @NotNull Path configPath,
            @NotNull C mainConfig,
            @NotNull Map<Path, P> presetByPath
    ) {}
}
