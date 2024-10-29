package com.naocraftlab.foggypalegarden.config.main;

import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenConfigurationException;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MainConfig {

    /**
     * Config schema version.
     */
    private final int version;

    protected MainConfig() {
        this(-1);
    }

    public void validate() {
        if (version < 1 || version > 3) {
            throw new FoggyPaleGardenConfigurationException("Unsupported config version (" + version + ")");
        }
    }
}
