package com.naocraftlab.foggypalegarden.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.naocraftlab.foggypalegarden.fabric.command.FpgNoFogGameModeCommand;
import com.naocraftlab.foggypalegarden.fabric.command.FpgPresetCommand;
import com.naocraftlab.foggypalegarden.fabric.command.FpgReloadConfigCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;

import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
public final class FoggyPaleGardenClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        FpgPresetCommand.register(dispatcher);
        FpgReloadConfigCommand.register(dispatcher);
        FpgNoFogGameModeCommand.register(dispatcher);
    }
}
