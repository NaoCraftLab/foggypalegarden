package com.naocraftlab.foggypalegarden.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naocraftlab.foggypalegarden.config.ModConfigV1.FogSettings;
import com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset;
import com.naocraftlab.foggypalegarden.util.FoggyPaleGardenException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.naocraftlab.foggypalegarden.FoggyPaleGardenClientMod.MOD_ID;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.AMBIANCE;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.I_AM_NOT_AFRAID_BUT;
import static com.naocraftlab.foggypalegarden.config.ModConfigV1.ForPreset.STEPHEN_KING;
import static com.naocraftlab.foggypalegarden.util.Files.readString;
import static com.naocraftlab.foggypalegarden.util.Files.writeString;
import static java.nio.file.Files.exists;

public class ConfigManager {

    public static final Path CONFIG_PATH = Paths.get("./config/" + MOD_ID + ".json");
    private static final int CURRENT_CONFIG_VERSION = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final Set<String> DEFAULT_SUPPORTED_BIOME_IDS = new HashSet<>();

    static {
        DEFAULT_SUPPORTED_BIOME_IDS.add("minecraft:pale_garden");
    }

    public static final Map<ForPreset, FogSettings> FOG_PRESETS = new HashMap<>();

    static {
        FOG_PRESETS.put(AMBIANCE, new FogSettings(
                2.0f,
                4,
                15.0f,
                15,
                95,
                6
        ));
        FOG_PRESETS.put(I_AM_NOT_AFRAID_BUT, new FogSettings(
                2.0f,
                4,
                15.0f,
                15,
                100,
                6
        ));
        FOG_PRESETS.put(STEPHEN_KING, new FogSettings(
                0.0f,
                4,
                10.0f,
                15,
                100,
                6
        ));
    }

    private static final ModConfigV1 DEFAULT = new ModConfigV1(
            CURRENT_CONFIG_VERSION,
            DEFAULT_SUPPORTED_BIOME_IDS,
            STEPHEN_KING,
            FOG_PRESETS.get(STEPHEN_KING)
    );
    private static ModConfigV1 current = null;

    public static ModConfigV1 currentConfig() {
        if (current == null) {
            reloadConfig();
        }
        return current;
    }

    public static void reloadConfig() {
        if (exists(CONFIG_PATH)) {
            final String configContent = migrateConfig(readString(CONFIG_PATH));
            current = GSON.fromJson(configContent, ModConfigV1.class);
            return;
        }
        saveConfig(DEFAULT);
    }

    public static void saveConfig(ModConfigV1 config) {
        current = config;
        writeString(CONFIG_PATH, GSON.toJson(current));
    }

    private static String migrateConfig(String configContent) {
        final int configVersion = GSON.fromJson(configContent, ModConfig.class).getVersion();
        if (configVersion <= 0) {
            throw new FoggyPaleGardenException(
                    "Incorrect configuration file (" + CONFIG_PATH.toAbsolutePath() + "). Fix or remove it and start the game again!"
            );
        } else if (configVersion != CURRENT_CONFIG_VERSION) {
            throw new FoggyPaleGardenException(
                    "Version (" + configVersion + ") of config file (" + CONFIG_PATH.toAbsolutePath()
                            + ") is higher than what is supported (" + CURRENT_CONFIG_VERSION + ") by mod (" + MOD_ID + ")"
            );
        }
        return configContent;
    }
}
