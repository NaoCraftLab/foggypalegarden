package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Color {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
}
