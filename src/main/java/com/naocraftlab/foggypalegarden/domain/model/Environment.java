package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import net.minecraft.world.Difficulty;

@Builder
public record Environment(
        String biome,
        Difficulty difficulty,
        Weather weather,
        long timeOfDay,
        int skyLightLevel,
        double heightAboveSurface,
        Color gameFogColor,
        float fogDensity
) {
}
