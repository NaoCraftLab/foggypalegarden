package com.naocraftlab.foggypalegarden.command;

import java.util.List;

public interface FpgCommand {

    String BASE_COMMAND = "fpg";
    String PRESET_ARGUMENT = "preset";
    String RELOAD_CONFIG_ARGUMENT = "reloadConfig";
    List<String> ALL_ARGUMENTS = List.of(PRESET_ARGUMENT, RELOAD_CONFIG_ARGUMENT);
}
