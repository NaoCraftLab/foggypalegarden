package com.naocraftlab.foggypalegarden.fabric;

import com.naocraftlab.foggypalegarden.command.FpgCommand;
import com.naocraftlab.foggypalegarden.command.FpgNoFogGameModeCommand;
import com.naocraftlab.foggypalegarden.command.FpgPresetCommand;
import com.naocraftlab.foggypalegarden.command.FpgReloadConfigCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
public final class FoggyPaleGardenClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(FpgCommand.INSTANCE);
            dispatcher.register(FpgPresetCommand.INSTANCE);
            dispatcher.register(FpgNoFogGameModeCommand.INSTANCE);
            dispatcher.register(FpgReloadConfigCommand.INSTANCE);
        });
    }
}
