package com.naocraftlab.foggypalegarden.config.main;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.world.GameMode;

import java.util.Set;

@Data
@With
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MainConfigV3 extends MainConfig {

    private final String preset;

    private final Set<GameMode> noFogGameModes;

    public MainConfigV3(String preset, Set<GameMode> noFogGameModes) {
        super(3);
        this.preset = preset;
        this.noFogGameModes = noFogGameModes;
    }

    public Set<GameMode> getNoFogGameModes() {
        return noFogGameModes == null ? Set.of() : noFogGameModes;
    }
}