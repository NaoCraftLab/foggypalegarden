package com.naocraftlab.foggypalegarden.neoforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.naocraftlab.foggypalegarden.command.FpgCommand;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

@EventBusSubscriber
public final class FpgReloadConfigCommand implements FpgCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal(BASE_COMMAND)
                        .then(Commands.literal(RELOAD_CONFIG_ARGUMENT).executes(FpgReloadConfigCommand::reloadConfig))
        );
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        try {
            configFacade().load();
            val currentPreset = configFacade().getCurrentPreset().getCode();
            val allPresets = String.join("\n", configFacade().getAvailablePresetCodes());
            context.getSource().sendSuccess(
                    () -> Component.translatable("fpg.command.reloadConfig.success", currentPreset, allPresets)
                            .withStyle(style -> style.withColor(GREEN)),
                    false
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.translatable("fpg.command.reloadConfig.exception", e.getMessage())
                            .withStyle(style -> style.withColor(RED))
            );
            return 0;
        }
    }
}
