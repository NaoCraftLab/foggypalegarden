package com.naocraftlab.foggypalegarden.config;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ModConfigV2 extends ModConfig {

    private final String preset;

    public ModConfigV2(String preset) {
        super(2);
        this.preset = preset;
    }
}
