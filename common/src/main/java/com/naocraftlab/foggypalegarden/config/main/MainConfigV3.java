package com.naocraftlab.foggypalegarden.config.main;

import com.naocraftlab.foggypalegarden.domain.model.FpgGameMode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;

import java.util.Set;

@Data
@With
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MainConfigV3 extends MainConfig {

    private final String preset;

    private final Set<FpgGameMode> noFogGameModes;

    public MainConfigV3(String preset, Set<FpgGameMode> noFogGameModes) {
        super(3);
        this.preset = preset;
        this.noFogGameModes = noFogGameModes;
    }

    public Set<FpgGameMode> getNoFogGameModes() {
        return noFogGameModes == null ? Set.of() : noFogGameModes;
    }
}
