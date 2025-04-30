package com.naocraftlab.foggypalegarden.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.naocraftlab.foggypalegarden.AbstractCommand;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.stream.Collectors;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

public class FpgReloadConfigCommand extends AbstractCommand {

    public static final LiteralArgumentBuilder<CommandSourceStack> INSTANCE = Commands.literal(BASE_COMMAND).then(
            Commands.literal(RELOAD_CONFIG_COMMAND).executes(FpgReloadConfigCommand::reloadConfig)
    );

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        try {
            configFacade().load();
            val currentPreset = configFacade().getCurrentPreset().getCode();
            val allPresets = configFacade().getAvailablePresetCodes().stream().collect(Collectors.joining("\n"));
            context.getSource().sendSuccess(
                    () -> Component.translatable("fpg.command.reloadConfig.success", currentPreset, allPresets).withStyle(GREEN),
                    false
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.translatable("fpg.command.reloadConfig.exception", e.getMessage()).withStyle(RED)
            );
            return 0;
        }
    }
}
