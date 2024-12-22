package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import net.minecraft.world.Difficulty;

@Builder
public record Environment(
        String dimension,
        String biome,
        Float biomeTemperature,
        Difficulty difficulty,
        Weather weather,
        long timeOfDay,
        int skyLightLevel,
        double height,
        double heightAboveSurface,
        Color gameFogColor,
        float fogDensity
) {
}
