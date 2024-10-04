package com.naocraftlab.foggypalegarden;

import net.fabricmc.api.ClientModInitializer;

import static com.naocraftlab.foggypalegarden.config.ConfigManager.reloadConfig;

public class FoggyPaleGardenClientMod implements ClientModInitializer {

    public static final String MOD_ID = "foggy-pale-garden";

    @Override
    public void onInitializeClient() {
        reloadConfig();
    }
}
