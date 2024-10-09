package com.naocraftlab.foggypalegarden.config;

import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenConfigurationException;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FogPreset {

    /**
     * Preset schema version.
     */
    private final int version;

    protected FogPreset() {
        this(-1);
    }

    public void validate() {
        if (version != 2) {
            throw new FoggyPaleGardenConfigurationException("Unsupported preset version (" + version + ")");
        }
    }
}
