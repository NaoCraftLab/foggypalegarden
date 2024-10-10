package com.naocraftlab.foggypalegarden;

import com.naocraftlab.foggypalegarden.command.FpgPresetCommand;
import com.naocraftlab.foggypalegarden.command.FpgReloadConfigCommand;
import com.naocraftlab.foggypalegarden.config.ConfigManager;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

public class FoggyPaleGardenClientMod implements ClientModInitializer {

    public static final String MOD_ID = "foggy-pale-garden";

    @Override
    public void onInitializeClient() {
        ConfigManager.init(FabricLoader.getInstance().getConfigDir(), MOD_ID);
        ConfigManager.registerListener(FogService::onConfigChange);
        ConfigManager.reloadConfigs();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            FpgPresetCommand.register(dispatcher);
            FpgReloadConfigCommand.register(dispatcher);
        });
    }
}
