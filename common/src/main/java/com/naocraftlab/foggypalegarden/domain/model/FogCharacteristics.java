package com.naocraftlab.foggypalegarden.domain.model;

import com.mojang.blaze3d.shaders.FogShape;
import lombok.Builder;

@Builder
public record FogCharacteristics(
        float startDistance,
        float endDistance,
        Color color,
        FogShape shape,
        float fogDensity
) {
}
