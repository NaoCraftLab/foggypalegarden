package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class FogCharacteristics {

    private final float startDistance;
    private final float endDistance;
    private final Color color;
    private final FogShape shape;
    private final float fogDensity;

    public float startDistance() {
        return startDistance;
    }

    public float endDistance() {
        return endDistance;
    }

    public Color color() {
        return color;
    }

    public FogShape shape() {
        return shape;
    }

    public float fogDensity() {
        return fogDensity;
    }
}
