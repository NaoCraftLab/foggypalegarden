package com.naocraftlab.foggypalegarden;

import com.naocraftlab.foggypalegarden.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FoggyPaleGardenClientMod implements ClientModInitializer {

    public static final String MOD_ID = "foggy-pale-garden";

    @Override
    public void onInitializeClient() {
        ConfigManager.init(FabricLoader.getInstance().getConfigDir(), MOD_ID);
        ConfigManager.reloadConfigs();
    }
}
