package com.naocraftlab.foggypalegarden.command;

import java.util.List;

public abstract class AbstractCommand {

    protected static String BASE_COMMAND = "fpg";

    protected static String PRESET_COMMAND = "preset";
    protected static String RELOAD_CONFIG_COMMAND = "reloadConfig";
    protected static String NO_FOG_GAME_MODE_COMMAND = "noFogGameMode";

    protected static List<String> ALL_COMMANDS = List.of(
            PRESET_COMMAND,
            RELOAD_CONFIG_COMMAND,
            NO_FOG_GAME_MODE_COMMAND
    );
}
