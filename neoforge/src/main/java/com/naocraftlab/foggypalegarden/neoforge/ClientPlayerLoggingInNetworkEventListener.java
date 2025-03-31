package com.naocraftlab.foggypalegarden.neoforge;

import lombok.val;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import static com.naocraftlab.foggypalegarden.chat.MessageSender.DISABLE_FOG_OCCLUSION;
import static com.naocraftlab.foggypalegarden.chat.MessageSender.sendToClientChat;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isEmbeddiumFogOcclusionEnabled;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isRubidiumFogOcclusionEnabled;
import static com.naocraftlab.foggypalegarden.checker.FogOcclusionChecker.isSodiumFogOcclusionEnabled;

@EventBusSubscriber
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
