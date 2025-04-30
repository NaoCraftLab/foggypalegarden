package com.naocraftlab.foggypalegarden.config.presetsource;

import com.naocraftlab.foggypalegarden.config.preset.FogPreset;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.List;

public interface PresetSource<T extends FogPreset> {

    PresetSourceType type();
    
    List<PresetBox<T>> load();

    void save(Collection<PresetBox<T>> presets);

    enum PresetSourceType {
        EMBEDDED,
        CONFIG,
        RESOURCE_PACK
    }

    @Data
    @Builder
    class PresetBox<T extends FogPreset> {
        private final PresetSourceType sourceType;
        private final String code;
        private final String path;
        private final T preset;
    }
}
