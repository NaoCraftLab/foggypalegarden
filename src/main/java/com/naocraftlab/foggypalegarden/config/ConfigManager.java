package com.naocraftlab.foggypalegarden.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding;
import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding.Brightness;
import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding.Color;
import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding.Condition;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenConfigurationException;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenEnvironmentException;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenException;
import com.naocraftlab.foggypalegarden.util.FpgFiles;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.world.Difficulty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.naocraftlab.foggypalegarden.FoggyPaleGardenClientMod.MOD_ID;
import static com.naocraftlab.foggypalegarden.util.FpgCollections.treeSetOf;

@UtilityClass
public final class ConfigManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final int CURRENT_CONFIG_VERSION = 2;
    private static final ModConfigV2 DEFAULT_CONFIG = ModConfigV2.builder().preset("FPG_STEPHEN_KING").build();
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
                                            Condition.builder().difficultyIn(treeSetOf(Difficulty.PEACEFUL, Difficulty.EASY)).build(),
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
                                                    Condition.builder().difficultyIn(treeSetOf(Difficulty.NORMAL)).build(),
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
                                                    Condition.builder().difficultyIn(treeSetOf(Difficulty.HARD)).build(),
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

    private static Path configFilePtah;
    private static Path presetDirectoryPath;

    private static ModConfigV2 currentConfig = null;
    private static Map<String, Pair<Path, FogPresetV2>> allPresets = null;

    private static List<BiConsumer<ModConfigV2, Map<String, Pair<Path, FogPresetV2>>>> listeners = new ArrayList<>();


    public static void init(Path configDirectory, String modId) {
        configFilePtah = configDirectory.resolve(Paths.get(modId + ".json"));
        presetDirectoryPath = configDirectory.resolve(Paths.get(modId.replaceAll("-", "")));
    }

    public static void reloadConfigs() {
        if (!Files.exists(configFilePtah)) {
            saveConfig(DEFAULT_CONFIG);
        } else {
            final String configContent = migrateConfigs(FpgFiles.readString(configFilePtah));
            currentConfig = GSON.fromJson(configContent, ModConfigV2.class);
        }

        if (FpgFiles.createDirectories(presetDirectoryPath)) {
            savePresets(DEFAULT_PRESETS);
            return;
        }
        val presets = new HashMap<Path, FogPresetV2>();
        try (val stream = Files.newDirectoryStream(presetDirectoryPath, "*.json")) {
            for (val path : stream) {
                migratePreset(path, FpgFiles.readString(path))
                        .map(presetContent -> GSON.fromJson(presetContent, FogPresetV2.class))
                        .ifPresent(preset -> presets.put(path, preset));
            }
        } catch (IOException e) {
            throw new FoggyPaleGardenEnvironmentException("Failed to process files in directory (" + presetDirectoryPath + ")", e);
        }
        allPresets = presets.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().getCode(),
                        entry -> new Pair<>(entry.getKey(), entry.getValue())
                ));

        validate();
        notifyListeners();
    }


    public static ModConfigV2 currentConfig() {
        if (currentConfig == null) {
            reloadConfigs();
        }
        return currentConfig;
    }

    public static void saveConfig(ModConfigV2 config) {
        currentConfig = config;
        FpgFiles.writeString(configFilePtah, GSON.toJson(currentConfig));
        notifyListeners();
    }

    public static Map<String, Pair<Path, FogPresetV2>> allPresets() {
        if (allPresets == null) {
            reloadConfigs();
        }
        return allPresets;
    }

    public static void savePresets(List<FogPresetV2> presets) {
        ConfigManager.allPresets = presets.stream()
                .collect(Collectors.toMap(
                        FogPresetV2::getCode,
                        preset -> new Pair<>(presetDirectoryPath.resolve(preset.getCode() + ".json"), preset)
                ));
        FpgFiles.createDirectories(presetDirectoryPath);
        removeAllPresets();
        ConfigManager.allPresets.forEach((code, pair) -> FpgFiles.writeString(pair.first(), GSON.toJson(pair.second())));
        notifyListeners();
    }


    private static void validate() {
        val selectedPreset = currentConfig.getPreset();
        if (!allPresets.containsKey(selectedPreset)) {
            throw new FoggyPaleGardenConfigurationException(
                    "Selected preset (" + selectedPreset + ") in the config (" + configFilePtah.toAbsolutePath() + ") is not found"
            );
        }
        currentConfig.validate();
        for (val presetByPath : allPresets.values()) {
            try {
                presetByPath.second().validate();
            } catch (FoggyPaleGardenException e) {
                throw new FoggyPaleGardenConfigurationException("Invalid preset (" + presetByPath.first().toAbsolutePath() + ")", e);
            }
        }
    }

    private static String migrateConfigs(String configContent) {
        final int configVersion = GSON.fromJson(configContent, ModConfig.class).getVersion();
        if (configVersion <= 0) {
            throw new FoggyPaleGardenConfigurationException(
                    "Incorrect configuration file (" + configFilePtah.toAbsolutePath() + "). Fix or remove it and start the game again!"
            );
        }
        if (configVersion == 1) {
            return migrateConfigV1toV2(configContent);
        } else if (configVersion != CURRENT_CONFIG_VERSION) {
            throw new FoggyPaleGardenConfigurationException(
                    "Version (" + configVersion + ") of config file (" + configFilePtah.toAbsolutePath()
                            + ") is higher than what is supported (" + CURRENT_CONFIG_VERSION + ") by mod (" + MOD_ID + ")"
            );
        }

        return configContent;
    }

    private static String migrateConfigV1toV2(String configContent) {
        val configV1 = GSON.fromJson(configContent, ModConfigV1.class);
        final String preset;
        if (configV1.getFogPreset() == ModConfigV1.FogPreset.AMBIANCE) {
            preset = "FPG_AMBIANCE";
        } else if (configV1.getFogPreset() == ModConfigV1.FogPreset.I_AM_NOT_AFRAID_BUT) {
            preset = "FPG_I_AM_NOT_AFRAID_BUT";
        } else if (configV1.getFogPreset() == ModConfigV1.FogPreset.STEPHEN_KING) {
            preset = "FPG_STEPHEN_KING";
        } else if (configV1.getFogPreset() == ModConfigV1.FogPreset.DIFFICULTY_BASED) {
            preset = "FPG_DIFFICULTY_BASED";
        } else {
            preset = "CUSTOM";
        }
        val configV2 = ModConfigV2.builder().preset(preset).build();
        saveConfig(configV2);

        val customPreset = FogPresetV2.builder()
                .code("CUSTOM")
                .bindings(List.of(
                        Binding.builder()
                                .condition(Condition.builder().biomeIdIn(configV1.getBiomes()).build())
                                .startDistance(configV1.getCustomFog().startDistance())
                                .skyLightStartLevel(configV1.getCustomFog().skyLightStartLevel())
                                .endDistance(configV1.getCustomFog().endDistance())
                                .surfaceHeightEnd(configV1.getCustomFog().surfaceHeightEnd())
                                .opacity(configV1.getCustomFog().opacity())
                                .encapsulationSpeed(configV1.getCustomFog().encapsulationSpeed())
                                .brightness(Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                .color(Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                .build()
                )).build();
        savePresets(Stream.concat(DEFAULT_PRESETS.stream(), Stream.of(customPreset)).collect(Collectors.toList()));

        return GSON.toJson(configV2);
    }

    private static Optional<String> migratePreset(Path path, String presetContent) {
        val preset = GSON.fromJson(presetContent, FogPreset.class);
        if (preset.getVersion() == 2) {
            return Optional.of(presetContent);
        }
        if (preset.getVersion() > 0) {
            throw new FoggyPaleGardenConfigurationException(
                    "Version (" + preset.getVersion() + ") of preset file (" + path.toAbsolutePath()
                            + ") is higher than what is supported (" + CURRENT_CONFIG_VERSION + ") by mod (" + MOD_ID + ")"
            );
        }

        LOG.warn("Unsupported preset file ({}) by mod ({})", path.toAbsolutePath(), MOD_ID);
        return Optional.empty();
    }

    private static void removeAllPresets() {
        try (val stream = Files.newDirectoryStream(presetDirectoryPath, "*.json")) {
            for (val path : stream) {
                val preset = GSON.fromJson(FpgFiles.readString(path), FogPreset.class);
                if (0 < preset.getVersion() && preset.getVersion() <= CURRENT_CONFIG_VERSION) {
                    Files.delete(path);
                }
            }
        } catch (IOException e) {
            throw new FoggyPaleGardenEnvironmentException("Failed to process files in directory (" + presetDirectoryPath + ")", e);
        }
    }

    private static void notifyListeners() {
        for (val listener : listeners) {
            listener.accept(currentConfig, allPresets);
        }
    }

    public static void registerListener(BiConsumer<ModConfigV2, Map<String, Pair<Path, FogPresetV2>>> listener) {
        listeners.add(listener);
    }
}
