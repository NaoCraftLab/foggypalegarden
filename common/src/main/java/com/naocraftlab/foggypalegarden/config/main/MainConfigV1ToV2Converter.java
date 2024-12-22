package com.naocraftlab.foggypalegarden.config.main;

import com.naocraftlab.foggypalegarden.config.main.MainConfigV1.FogPreset;
import com.naocraftlab.foggypalegarden.util.Converter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.naocraftlab.foggypalegarden.config.main.MainConfigV1.FogPreset.CUSTOM;

public final class MainConfigV1ToV2Converter implements Converter<MainConfig, MainConfig> {

    @Override
    public @NotNull MainConfig convert(@NotNull MainConfig source) {
        val mainConfigV1 = (MainConfigV1) source;
        return MainConfigV2.builder()
                .preset(toPreset(mainConfigV1.getFogPreset()))
                .noFogGameModes(Set.of())
                .build();
    }

    private String toPreset(FogPreset preset) {
        if (preset == CUSTOM) {
            return CUSTOM.name();
        }
        return "FPG_" + preset.name().toUpperCase();
    }
}
