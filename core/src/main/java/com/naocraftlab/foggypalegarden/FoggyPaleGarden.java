package com.naocraftlab.foggypalegarden;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FoggyPaleGarden {

    public static final String MOD_ID = "foggypalegarden";
    public static final String MOD_NAME = "Foggy Pale Garden";

    public static final Path CONFIG_DIR = Paths.get("./config");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
}
