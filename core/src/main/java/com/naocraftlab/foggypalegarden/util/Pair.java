package com.naocraftlab.foggypalegarden.util;

import lombok.Data;

@Data
public class Pair<F, S> {
    private final F first;
    private final S second;

    public F first() {
        return first;
    }

    public S second() {
        return second;
    }
}
