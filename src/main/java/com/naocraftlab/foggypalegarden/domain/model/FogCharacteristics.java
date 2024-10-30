package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import net.minecraft.client.render.FogShape;

@Builder
public record FogCharacteristics(
        float startDistance,
        float endDistance,
        Color color,
        FogShape shape,
        float fogDensity
) {
}
