package com.naocraftlab.foggypalegarden.domain.model;

import com.mojang.blaze3d.shaders.FogShape;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FogCharacteristics {
    private final float startDistance;
    private final float endDistance;
    private final Color color;
    private final FogShape shape;
    private final float fogDensity;
}
