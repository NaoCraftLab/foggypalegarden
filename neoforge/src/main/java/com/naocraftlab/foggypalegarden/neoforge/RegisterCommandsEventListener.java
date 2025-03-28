package com.naocraftlab.foggypalegarden.neoforge;

import com.naocraftlab.foggypalegarden.command.FpgCommand;
import com.naocraftlab.foggypalegarden.command.FpgNoFogGameModeCommand;
import com.naocraftlab.foggypalegarden.command.FpgPresetCommand;
import com.naocraftlab.foggypalegarden.command.FpgReloadConfigCommand;
import lombok.val;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class RegisterCommandsEventListener {

    @SubscribeEvent
    public static void listen(RegisterCommandsEvent event) {
        val dispatcher = event.getDispatcher();
        dispatcher.register(FpgCommand.INSTANCE);
        dispatcher.register(FpgPresetCommand.INSTANCE);
        dispatcher.register(FpgNoFogGameModeCommand.INSTANCE);
        dispatcher.register(FpgReloadConfigCommand.INSTANCE);
    }
}
