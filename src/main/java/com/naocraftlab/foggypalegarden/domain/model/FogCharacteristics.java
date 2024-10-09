package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;

@Builder
public record FogCharacteristics(
        float startDistance,
        float endDistance,
        Color color,
        float fogDensity
) {
}
