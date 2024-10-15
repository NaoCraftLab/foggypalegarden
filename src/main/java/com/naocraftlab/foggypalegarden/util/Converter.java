package com.naocraftlab.foggypalegarden.util;

import org.jetbrains.annotations.NotNull;

public interface Converter<S, T> {

    @NotNull T convert(@NotNull S source);
}
