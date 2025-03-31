package com.naocraftlab.foggypalegarden.config.presetsource;

import com.naocraftlab.foggypalegarden.config.preset.FogPreset;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenEnvironmentException;
import com.naocraftlab.foggypalegarden.util.FpgFiles;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.CONFIG_DIR;
import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.GSON;
import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.MOD_ID;
import static com.naocraftlab.foggypalegarden.config.presetsource.PresetSource.PresetSourceType.CONFIG;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.list;

@RequiredArgsConstructor
public final class PresetSourceConfig<T extends FogPreset> implements PresetSource<T> {

    public static final Path PRESET_DIR_PATH = CONFIG_DIR.resolve(MOD_ID);

    private final int presetVersion;

    private final Class<T> presetType;

    @Override
    public PresetSourceType type() {
        return CONFIG;
    }

    @Override
    public List<PresetBox<T>> load() {
        if (exists(PRESET_DIR_PATH)) {
            try (val stream = list(PRESET_DIR_PATH)) {
                return stream
                        .filter(file -> isRegularFile(file) && file.getFileName().toString().endsWith(".json"))
                        .map(path -> new Pair<>(path, GSON.fromJson(FpgFiles.readString(path), FogPreset.class)))
                        .filter(pair -> pair.second().getVersion() == presetVersion)
                        .map(pair -> new Pair<>(pair.first(), GSON.fromJson(FpgFiles.readString(pair.first()), presetType)))
                        .map(pair ->
                            PresetBox.<T>builder()
                                    .sourceType(type())
                                    .code(pair.second().getCode())
                                    .path(pair.first().toString())
                                    .preset(pair.second())
                                    .build()
                        ).toList();
            } catch (Exception e) {
                throw new FoggyPaleGardenEnvironmentException("Failed to read presets", e);
            }
        } else {
            FpgFiles.createDirectories(PRESET_DIR_PATH);
            return List.of();
        }
    }

    @Override
    public void save(Collection<PresetBox<T>> presets) {
        for (val presetBox : presets) {
            if (presetBox.getSourceType() != CONFIG) {
                continue;
            }
            FpgFiles.writeString(PRESET_DIR_PATH.resolve(presetBox.getCode() + ".json"), GSON.toJson(presetBox.getPreset()));
        }
    }
}
