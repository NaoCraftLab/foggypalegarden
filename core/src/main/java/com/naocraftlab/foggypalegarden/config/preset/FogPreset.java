package com.naocraftlab.foggypalegarden.config.preset;

import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenConfigurationException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class FogPreset {

    /**
     * Preset schema version.
     */
    private final int version;

    /**
     * Unique preset code.
     */
    protected final String code;

    protected FogPreset() {
        this(-1, "");
    }

    public void validate() {
        if (version < 2 || version > 3) {
            throw new FoggyPaleGardenConfigurationException("Unsupported preset version (" + version + ")");
        }
    }
}
