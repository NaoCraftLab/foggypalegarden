package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;

@Builder
public record Environment(
        String dimension,
        String biome,
        Float biomeTemperature,
        FpgDifficulty difficulty,
        Weather weather,
        long timeOfDay,
        int skyLightLevel,
        double height,
        double heightAboveSurface,
        Color gameFogColor,
        float fogDensity
) {
}
