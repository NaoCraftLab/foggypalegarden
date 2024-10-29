package com.naocraftlab.foggypalegarden.config.main;

import com.naocraftlab.foggypalegarden.util.Converter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public final class MainConfigV2ToV3Converter implements Converter<MainConfig, MainConfig> {

    @Override
    public @NotNull MainConfig convert(@NotNull MainConfig source) {
        val mainConfigV2 = (MainConfigV2) source;
        return MainConfigV3.builder()
                .preset(mainConfigV2.getPreset())
                .noFogGameModes(mainConfigV2.getNoFogGameModes())
                .build();
    }
}
