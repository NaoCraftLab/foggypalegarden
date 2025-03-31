package com.naocraftlab.foggypalegarden.fabric;

import com.naocraftlab.foggypalegarden.command.FpgCommand;
import com.naocraftlab.foggypalegarden.command.FpgNoFogGameModeCommand;
import com.naocraftlab.foggypalegarden.command.FpgPresetCommand;
import com.naocraftlab.foggypalegarden.command.FpgReloadConfigCommand;
import lombok.val;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import static com.naocraftlab.foggypalegarden.chat.MessageSender.DISABLE_FOG_OCCLUSION;
import static com.naocraftlab.foggypalegarden.chat.MessageSender.sendToClientChat;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isEmbeddiumFogOcclusionEnabled;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isRubidiumFogOcclusionEnabled;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isSodiumFogOcclusionEnabled;
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

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            val fabricLoader = FabricLoader.getInstance();
            if (
                    (fabricLoader.isModLoaded("sodium") && isSodiumFogOcclusionEnabled())
                            || (fabricLoader.isModLoaded("embeddium") && isEmbeddiumFogOcclusionEnabled())
                            || (fabricLoader.isModLoaded("rubidium") && isRubidiumFogOcclusionEnabled())
            ) {
                sendToClientChat(DISABLE_FOG_OCCLUSION);
            }
        });
    }
}
