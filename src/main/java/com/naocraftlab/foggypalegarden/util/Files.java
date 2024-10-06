package com.naocraftlab.foggypalegarden.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Path;

import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public final class Files {

    private Files() {
        throw new UnsupportedOperationException();
    }

    public static String readString(Path path) {
        final StringBuilder stringBuilder = new StringBuilder();
        try (final BufferedReader reader = newBufferedReader(path, UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(lineSeparator());
            }
        } catch (Exception e) {
            throw new FoggyPaleGardenException("Failed to read file (" + path.toAbsolutePath() + ")", e);
        }
        return stringBuilder.toString();
    }

    public static Path writeString(Path path, CharSequence charSequence) {
        try {
            createDirectories(path.getParent());
            try (final BufferedWriter writer = newBufferedWriter(path, UTF_8, CREATE, TRUNCATE_EXISTING)) {
                writer.append(charSequence);
            }
        } catch (Exception e) {
            throw new FoggyPaleGardenException("Failed to write file (" + path.toAbsolutePath() + ")", e);
        }
        return path;
    }
}
