package com.naocraftlab.foggypalegarden.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.naocraftlab.foggypalegarden.AbstractCommand;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

public class FpgPresetCommand extends AbstractCommand {

    private static final SuggestionProvider<CommandSourceStack> PRESET_SUGGESTIONS = (context, builder) -> {
        val presets = configFacade().getAvailablePresetCodes();
        return SharedSuggestionProvider.suggest(presets, builder);
    };

    public static final LiteralArgumentBuilder<CommandSourceStack> INSTANCE = Commands.literal(BASE_COMMAND).then(
            Commands.literal(PRESET_COMMAND).then(
                    Commands.argument(PRESET_COMMAND, string()).suggests(PRESET_SUGGESTIONS).executes(FpgPresetCommand::setPreset)
            ).executes(context -> {
                val currentPreset = configFacade().getCurrentPreset().getCode();
                context.getSource().sendSuccess(() -> Component.translatable("fpg.command.preset.current", currentPreset), false);
                return 1;
            })
    );

    private static int setPreset(CommandContext<CommandSourceStack> context) {
        val preset = StringArgumentType.getString(context, PRESET_COMMAND);
        try {
            if (configFacade().setCurrentPreset(preset)) {
                context.getSource().sendSuccess(
                        () -> Component.translatable("fpg.command.preset.applied", preset).withStyle(GREEN),
                        false
                );
                configFacade().save();
                return 1;
            } else {
                val allPresets = String.join("\n", configFacade().getAvailablePresetCodes());
                context.getSource().sendFailure(
                        Component.translatable("fpg.command.preset.notFound", preset, allPresets).withStyle(RED)
                );
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.translatable("fpg.command.preset.exception", e.getMessage()).withStyle(RED)
            );
            return 0;
        }
    }
}
