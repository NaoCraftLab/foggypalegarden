package com.naocraftlab.foggypalegarden.config;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.minecraft.world.GameMode;

import java.util.Set;

@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ModConfigV2 extends ModConfig {

    private final String preset;

    private final Set<GameMode> noFogGameModes;

    public ModConfigV2(String preset, Set<GameMode> noFogGameModes) {
        super(2);
        this.preset = preset;
        this.noFogGameModes = noFogGameModes;
    }

    public Set<GameMode> getNoFogGameModes() {
        return noFogGameModes == null ? Set.of() : noFogGameModes;
    }
}
