package com.naocraftlab.foggypalegarden.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lombok.val;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.naocraftlab.foggypalegarden.FoggyPaleGardenClientMod.configFacade;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class FpgReloadConfigCommand implements FpgCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal(BASE_COMMAND)
                        .then(ClientCommandManager.literal(RELOAD_CONFIG_ARGUMENT).executes(FpgReloadConfigCommand::reloadConfig))
        );
    }

    private static int reloadConfig(CommandContext<FabricClientCommandSource> context) {
        try {
            configFacade().load();
            val currentPreset = configFacade().getCurrentPreset().getCode();
            val allPresets = String.join("\n", configFacade().getAvailablePresetCodes());
            context.getSource().sendFeedback(
                    Text.translatable("fpg.command.reloadConfig.success", currentPreset, allPresets).formatted(GREEN)
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.translatable("fpg.command.reloadConfig.exception", e.getMessage()).formatted(RED));
            return 0;
        }
    }
}
