package com.naocraftlab.foggypalegarden.forge;

import com.naocraftlab.foggypalegarden.command.FpgCommand;
import com.naocraftlab.foggypalegarden.command.FpgNoFogGameModeCommand;
import com.naocraftlab.foggypalegarden.command.FpgPresetCommand;
import com.naocraftlab.foggypalegarden.command.FpgReloadConfigCommand;
import lombok.val;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
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
