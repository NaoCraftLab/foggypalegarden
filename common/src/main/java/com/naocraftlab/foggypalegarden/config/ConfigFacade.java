package com.naocraftlab.foggypalegarden.config;

import com.naocraftlab.foggypalegarden.config.main.MainConfig;
import com.naocraftlab.foggypalegarden.config.main.MainConfigV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.config.presetsource.PresetSource;
import com.naocraftlab.foggypalegarden.config.presetsource.PresetSource.PresetBox;
import com.naocraftlab.foggypalegarden.config.presetsource.PresetSource.PresetSourceType;
import com.naocraftlab.foggypalegarden.config.presetsource.PresetSourceConfig;
import com.naocraftlab.foggypalegarden.config.presetsource.PresetSourceEmbedded;
import com.naocraftlab.foggypalegarden.config.presetsource.PresetSourceResourcePack;
import com.naocraftlab.foggypalegarden.domain.model.GameType;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import com.naocraftlab.foggypalegarden.util.FpgFiles;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.CONFIG_DIR;
import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.GSON;
import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.MOD_ID;
import static com.naocraftlab.foggypalegarden.config.presetsource.PresetSourceConfig.PRESET_DIR_PATH;
import static com.naocraftlab.foggypalegarden.config.presetsource.PresetSourceEmbedded.DEFAULT_PRESET_CODE;
import static java.nio.file.Files.exists;

public final class ConfigFacade {

    private static final Path CONFIG_FILE_PTAH = CONFIG_DIR.resolve(MOD_ID + ".json");
    private static final int CURRENT_MAIN_CONFIG_VERSION = 3;
    private static final int CURRENT_PRESET_VERSION = 3;

    private static ConfigFacade instance = null;

    private final List<Consumer<FogPresetV3>> listeners;

    private final PresetSource<FogPresetV3> embeddedPresetSource = new PresetSourceEmbedded();
    private final PresetSource<FogPresetV3> resourcePackPresetSource =
            new PresetSourceResourcePack<>(CURRENT_PRESET_VERSION, FogPresetV3.class);
    private final PresetSource<FogPresetV3> configPresetSource = new PresetSourceConfig<>(CURRENT_PRESET_VERSION, FogPresetV3.class);

    private MainConfigV3 mainConfig = new MainConfigV3(DEFAULT_PRESET_CODE, Set.of());
    private Map<String, PresetBox<FogPresetV3>> presets = new HashMap<>();

    private ConfigFacade(List<Consumer<FogPresetV3>> listeners) {
        this.listeners = listeners;
        load();
    }

    @NotNull
    public static ConfigFacade configFacade() {
        if (instance == null) {
            instance = new ConfigFacade(List.of(FogService::onCurrentPresetChange));
        }
        return instance;
    }

    // files

    public void load() {
        loadConfig();
        loadPresets();
        backoffIfPresetDeleted();
        notifyCurrentPresetListeners(getCurrentPreset().getPreset());
    }

    private void backoffIfPresetDeleted() {
        if (!presets.containsKey(mainConfig.getPreset())) {
            mainConfig = mainConfig.withPreset(DEFAULT_PRESET_CODE);
            saveMainConfig();
        }
    }

    private void loadConfig() {
        if (exists(CONFIG_FILE_PTAH)) {
            val existsMainConfig = GSON.fromJson(FpgFiles.readString(CONFIG_FILE_PTAH), MainConfig.class);
            if (existsMainConfig.getVersion() == CURRENT_MAIN_CONFIG_VERSION) {
                mainConfig = GSON.fromJson(FpgFiles.readString(CONFIG_FILE_PTAH), MainConfigV3.class);
            }
        }
        saveMainConfig();
    }

    private void loadPresets() {
        presets = new HashMap<>();

        configPresetSource.load().forEach(preset -> presets.put(preset.getCode(), preset));
        resourcePackPresetSource.load().forEach(this::putOrReplaceToBackup);
        embeddedPresetSource.load().forEach(this::putOrReplaceToBackup);

        savePresets();
    }

    private void putOrReplaceToBackup(PresetBox<FogPresetV3> preset) {
        val exists = presets.get(preset.getCode());
        if (exists != null && exists.getSourceType() == PresetSourceType.CONFIG) {
            val backupPresetCode = exists.getCode() + "_BACKUP";
            val backupPresetPath = PRESET_DIR_PATH.resolve(backupPresetCode + ".json");
            FpgFiles.move(Paths.get(exists.getPath()), backupPresetPath);
            val backupPreset = PresetBox.<FogPresetV3>builder()
                    .sourceType(exists.getSourceType())
                    .code(backupPresetCode)
                    .path(backupPresetPath.toString())
                    .preset(exists.getPreset().withCode(backupPresetCode))
                    .build();
            presets.put(backupPreset.getCode(), backupPreset);
        }
        presets.put(preset.getCode(), preset);
    }

    public void save() {
        saveMainConfig();
        savePresets();
    }

    private void saveMainConfig() {
        FpgFiles.writeString(CONFIG_FILE_PTAH, GSON.toJson(mainConfig));
    }

    private void savePresets() {
        configPresetSource.save(presets.values());
    }

    // config

    @NotNull
    public List<GameType> noFogGameModes() {
        return mainConfig.getNoFogGameModes().stream().sorted().toList();
    }

    public boolean isNoFogGameMode(@NotNull GameType gameMode) {
        return mainConfig.getNoFogGameModes().contains(gameMode);
    }

    public void noFogGameModes(Set<GameType> gameModes) {
        mainConfig = mainConfig.withNoFogGameModes(gameModes);
    }

    public boolean toggleNoFogGameMode(@NotNull GameType gameMode) {
        val noFogGameModes = new HashSet<>(mainConfig.getNoFogGameModes());
        if (isNoFogGameMode(gameMode)) {
            noFogGameModes.remove(gameMode);
            mainConfig = mainConfig.withNoFogGameModes(noFogGameModes);
            return false;
        }
        noFogGameModes.add(gameMode);
        mainConfig = mainConfig.withNoFogGameModes(noFogGameModes);
        return true;
    }

    // presets

    @NotNull
    public List<String> getAvailablePresetCodes() {
        return presets.keySet().stream().sorted().toList();
    }

    @NotNull
    public PresetBox<FogPresetV3> getCurrentPreset() {
        return presets.get(mainConfig.getPreset());
    }

    public boolean setCurrentPreset(@NotNull String presetCode) {
        if (presets.containsKey(presetCode)) {
            mainConfig = mainConfig.withPreset(presetCode);
            notifyCurrentPresetListeners(getCurrentPreset().getPreset());
            return true;
        }
        return false;
    }

    // private

    private void notifyCurrentPresetListeners(FogPresetV3 currentPreset) {
        for (val listener : listeners) {
            listener.accept(currentPreset);
        }
    }
}
