package com.naocraftlab.foggypalegarden.forge;

import lombok.val;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import static com.naocraftlab.foggypalegarden.chat.MessageSender.DISABLE_FOG_OCCLUSION;
import static com.naocraftlab.foggypalegarden.chat.MessageSender.sendToClientChat;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isEmbeddiumFogOcclusionEnabled;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isRubidiumFogOcclusionEnabled;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isSodiumFogOcclusionEnabled;

@Mod.EventBusSubscriber
public class ClientPlayerLoggingInNetworkEventListener {

    @SubscribeEvent
    public static void listen(ClientPlayerNetworkEvent.LoggingIn event) {
        val modList = ModList.get();
        if (
                (modList.isLoaded("sodium") && isSodiumFogOcclusionEnabled())
                        || (modList.isLoaded("embeddium") && isEmbeddiumFogOcclusionEnabled())
                        || (modList.isLoaded("rubidium") && isRubidiumFogOcclusionEnabled())
        ) {
            sendToClientChat(DISABLE_FOG_OCCLUSION);
        }
    }
}
