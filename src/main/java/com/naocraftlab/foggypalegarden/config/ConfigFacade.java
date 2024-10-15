package com.naocraftlab.foggypalegarden.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naocraftlab.foggypalegarden.config.main.MainConfig;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPreset;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenEnvironmentException;
import com.naocraftlab.foggypalegarden.util.FpgFiles;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public final class ConfigFacade {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Path configFilePtah;
    private final Path presetDirectoryPath;
    private final ConfigMigrator configMigrator;

    private final List<Consumer<FogPresetV3>> listeners = new ArrayList<>();

    private MainConfigV3 mainConfig;
    private Map<String, Pair<Path, FogPresetV3>> presets;

    public void registerCurrentPresetListener(Consumer<FogPresetV3> listener) {
        listeners.add(listener);
    }

    // config

    public boolean isNoFogGameMode(@NotNull GameMode gameMode) {
        return mainConfig.getNoFogGameModes().contains(gameMode);
    }

    public boolean toggleNoFogGameMode(@NotNull GameMode gameMode) {
        val noFogGameModes = new HashSet<>(mainConfig.getNoFogGameModes());
        if (!noFogGameModes.contains(gameMode)) {
            noFogGameModes.add(gameMode);
            mainConfig = mainConfig.withNoFogGameModes(noFogGameModes);
            return true;
        }
        noFogGameModes.remove(gameMode);
        mainConfig = mainConfig.withNoFogGameModes(noFogGameModes);
        return false;
    }

    // presets

    @NotNull
    public List<String> getAvailablePresetCodes() {
        return presets.keySet().stream().sorted().toList();
    }

    @NotNull
    public FogPresetV3 getCurrentPreset() {
        return presets.get(mainConfig.getPreset()).second();
    }

    public boolean setCurrentPreset(@NotNull String presetCode) {
        if (presets.containsKey(presetCode)) {
            mainConfig = mainConfig.withPreset(presetCode);
            notifyCurrentPresetListeners(getCurrentPreset());
            return true;
        }
        return false;
    }

    // files

    public void load() {
        val loadedMainConfig = loadMainConfig(configFilePtah);
        val loadedPresets = loadPresets(presetDirectoryPath);

        val migrationResults = configMigrator.migrate(
                configFilePtah,
                presetDirectoryPath,
                loadedMainConfig,
                loadedPresets.stream().collect(toMap(Pair::first, Pair::second))
        );
        migrationResults.mainConfig().validate();
        migrationResults.presetByPath().forEach((path, preset) -> preset.validate());

        mainConfig = migrationResults.mainConfig();
        presets = migrationResults.presetByPath().entrySet().stream()
                .map(entry -> new Pair<>(entry.getValue().getCode(), new Pair<>(entry.getKey(), entry.getValue())))
                .collect(toMap(Pair::first, Pair::second));

        setCurrentPreset(mainConfig.getPreset());
        save();
    }

    public void save() {
        FpgFiles.writeString(configFilePtah, GSON.toJson(mainConfig));
        for (val preset : presets.values()) {
            FpgFiles.writeString(preset.first(), GSON.toJson(preset.second()));
        }
    }

    // private

    private void notifyCurrentPresetListeners(FogPresetV3 currentPreset) {
        for (val listener : listeners) {
            listener.accept(currentPreset);
        }
    }

    @Nullable
    private MainConfig loadMainConfig(@NotNull Path configFilePtah) {
        return exists(configFilePtah)
                ? GSON.fromJson(FpgFiles.readString(configFilePtah), MainConfig.class)
                : null;
    }

    @NotNull
    private List<Pair<Path, FogPreset>> loadPresets(@NotNull Path presetDirectoryPath) {
        if (exists(presetDirectoryPath)) {
            try (val stream = list(presetDirectoryPath)) {
                return stream
                        .filter(file -> isRegularFile(file) && file.getFileName().toString().endsWith(".json"))
                        .map(path -> new Pair<>(path, GSON.fromJson(FpgFiles.readString(path), FogPreset.class)))
                        .filter(pair -> pair.second().getVersion() > 0)
                        .toList();
            } catch (Exception e) {
                throw new FoggyPaleGardenEnvironmentException("Failed to read presets", e);
            }
        } else {
            return List.of();
        }
    }
}
