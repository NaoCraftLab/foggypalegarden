package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;

@Builder
public record Color(
        float red,
        float green,
        float blue,
        float alpha
) {
}
