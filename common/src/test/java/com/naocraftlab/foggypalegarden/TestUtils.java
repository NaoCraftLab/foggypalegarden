package com.naocraftlab.foggypalegarden;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@UtilityClass
public class TestUtils {

    public static long fileCount(Path directory) throws IOException {
        try (Stream<Path> files = Files.list(directory)) {
            return files.count();
        }
    }
}
