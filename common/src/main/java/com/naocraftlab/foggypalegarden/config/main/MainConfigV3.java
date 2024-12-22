package com.naocraftlab.foggypalegarden.config.main;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.world.level.GameType;

import java.util.Set;

@Data
@With
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MainConfigV3 extends MainConfig {

    private final String preset;

    private final Set<GameType> noFogGameModes;

    public MainConfigV3(String preset, Set<GameType> noFogGameModes) {
        super(3);
        this.preset = preset;
        this.noFogGameModes = noFogGameModes;
    }

    public Set<GameType> getNoFogGameModes() {
        return noFogGameModes == null ? Set.of() : noFogGameModes;
    }
}
