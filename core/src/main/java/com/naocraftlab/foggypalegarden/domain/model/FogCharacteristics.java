package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class FogCharacteristics {

    private final float startDistance;
    private final float endDistance;
    private final FogShape shape;

    public float startDistance() {
        return startDistance;
    }

    public float endDistance() {
        return endDistance;
    }

    public FogShape shape() {
        return shape;
    }
}
