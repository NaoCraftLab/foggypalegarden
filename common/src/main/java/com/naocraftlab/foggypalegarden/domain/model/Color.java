package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Color {

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public float red() {
        return red;
    }

    public float green() {
        return green;
    }

    public float blue() {
        return blue;
    }

    public float alpha() {
        return alpha;
    }
}
