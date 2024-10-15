package com.naocraftlab.foggypalegarden.util;

import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenEnvironmentException;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@UtilityClass
public class FpgFiles {

    public static String readString(Path path) {
        try {
            return Files.readString(path, UTF_8);
        } catch (Exception e) {
            throw new FoggyPaleGardenEnvironmentException("Failed to read file (" + path.toAbsolutePath() + ")", e);
        }
    }

    public static Path writeString(Path path, CharSequence charSequence) {
        try {
            createDirectories(path.getParent());
            Files.writeString(path, charSequence, UTF_8, CREATE, TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new FoggyPaleGardenEnvironmentException("Failed to write file (" + path.toAbsolutePath() + ")", e);
        }
        return path;
    }

    private static boolean createDirectories(Path path) {
        if (Files.exists(path)) {
            return false;
        }

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new FoggyPaleGardenEnvironmentException("Failed to create directory (" + path.toAbsolutePath() + ")", e);
        }
        return true;
    }
}
