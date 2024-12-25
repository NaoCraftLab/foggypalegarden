package com.naocraftlab.foggypalegarden.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naocraftlab.foggypalegarden.config.main.MainConfig;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPreset;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenEnvironmentException;
import com.naocraftlab.foggypalegarden.util.FpgFiles;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.MOD_ID;
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

    private static ConfigFacade instance = null;

    private static void init() {
        val configDir = Paths.get("./config");
        val configFilePtah = configDir.resolve(MOD_ID + ".json");
        val presetDirectoryPath = configDir.resolve(Paths.get(MOD_ID));
        instance = new ConfigFacade(configFilePtah, presetDirectoryPath, new ConfigMigrator(presetDirectoryPath));
        instance.registerCurrentPresetListener(FogService::onCurrentPresetChange);
        instance.load();
    }

    public static ConfigFacade configFacade() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public void registerCurrentPresetListener(Consumer<FogPresetV3> listener) {
        listeners.add(listener);
    }


    @NotNull
    public Path configFilePtah() {
        return configFilePtah;
    }

    @NotNull
    public Path presetDirectoryPath() {
        return presetDirectoryPath;
    }


    // config

    @NotNull
    public List<GameType> noFogGameModes() {
        return mainConfig.getNoFogGameModes().stream().sorted().toList();
    }

    public void noFogGameModes(Set<GameType> gameModes) {
        mainConfig = mainConfig.withNoFogGameModes(gameModes);
    }

    public boolean isNoFogGameMode(@NotNull GameType gameMode) {
        return mainConfig.getNoFogGameModes().contains(gameMode);
    }

    public boolean toggleNoFogGameMode(@NotNull GameType gameMode) {
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
        return presets.get(mainConfig.getPreset()).getSecond();
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
                loadedPresets.stream().collect(toMap(Pair::getFirst, Pair::getSecond))
        );
        migrationResults.getMainConfig().validate();
        migrationResults.getPresetByPath().forEach((path, preset) -> preset.validate());

        mainConfig = migrationResults.getMainConfig();
        presets = migrationResults.getPresetByPath().entrySet().stream()
                .map(entry -> new Pair<>(entry.getValue().getCode(), new Pair<>(entry.getKey(), entry.getValue())))
                .collect(toMap(Pair::getFirst, Pair::getSecond));

        setCurrentPreset(mainConfig.getPreset());
        save();
    }

    public void save() {
        FpgFiles.writeString(configFilePtah, GSON.toJson(mainConfig));
        for (val preset : presets.values()) {
            FpgFiles.writeString(preset.getFirst(), GSON.toJson(preset.getSecond()));
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
                        .filter(pair -> pair.getSecond().getVersion() > 0)
                        .toList();
            } catch (Exception e) {
                throw new FoggyPaleGardenEnvironmentException("Failed to read presets", e);
            }
        } else {
            return List.of();
        }
    }
}
