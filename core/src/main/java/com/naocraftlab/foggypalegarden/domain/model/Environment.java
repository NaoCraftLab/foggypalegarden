package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import lombok.Data;
import net.minecraft.world.Difficulty;

@Data
@Builder
public final class Environment {

    private final String dimension;
    private final String biome;
    private final Float biomeTemperature;
    private final Difficulty difficulty;
    private final Weather weather;
    private final long timeOfDay;
    private final int skyLightLevel;
    private final double height;
    private final double heightAboveSurface;
    private final Color gameFogColor;
    private final float fogDensity;

    public String dimension() {
        return dimension;
    }

    public String biome() {
        return biome;
    }

    public Float biomeTemperature() {
        return biomeTemperature;
    }

    public Difficulty difficulty() {
        return difficulty;
    }

    public Weather weather() {
        return weather;
    }

    public long timeOfDay() {
        return timeOfDay;
    }

    public int skyLightLevel() {
        return skyLightLevel;
    }

    public double height() {
        return height;
    }

    public double heightAboveSurface() {
        return heightAboveSurface;
    }

    public Color gameFogColor() {
        return gameFogColor;
    }

    public float fogDensity() {
        return fogDensity;
    }
}
