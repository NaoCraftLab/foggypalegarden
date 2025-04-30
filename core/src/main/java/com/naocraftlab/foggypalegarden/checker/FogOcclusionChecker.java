package com.naocraftlab.foggypalegarden.checker;

import com.google.gson.JsonParser;
import com.naocraftlab.foggypalegarden.util.FpgFiles;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.nio.file.Files;
import java.nio.file.Paths;

@UtilityClass
public class FogOcclusionChecker {

    public static boolean isSodiumFogOcclusionEnabled() {
        return isFogOcclusionEnabled("sodium");
    }

    public static boolean isEmbeddiumFogOcclusionEnabled() {
        return isFogOcclusionEnabled("embeddium");
    }

    public static boolean isRubidiumFogOcclusionEnabled() {
        return isFogOcclusionEnabled("rubidium");
    }

    private static boolean isFogOcclusionEnabled(String modId) {
        val optionsFile = Paths.get("./config").resolve(modId + "-options.json");
        if (!Files.exists(optionsFile)) {
            return false;
        }

        val optionsContent = FpgFiles.readString(optionsFile);
        val options = JsonParser.parseString(optionsContent).getAsJsonObject();
        return options.getAsJsonObject("performance").get("use_fog_occlusion").getAsBoolean();
    }
}
