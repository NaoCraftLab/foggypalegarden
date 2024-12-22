package com.naocraftlab.foggypalegarden.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.naocraftlab.foggypalegarden.command.FpgCommand;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.stream.Collectors;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

public class FpgPresetCommand implements FpgCommand {

    private static final SuggestionProvider<CommandSourceStack> PRESET_SUGGESTIONS = (context, builder) -> {
        val presets = configFacade().getAvailablePresetCodes();
        return SharedSuggestionProvider.suggest(presets, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        val command = net.minecraft.commands.Commands.literal(BASE_COMMAND)
                .then(net.minecraft.commands.Commands.literal(PRESET_ARGUMENT)
                        .then(net.minecraft.commands.Commands.argument(PRESET_ARGUMENT, StringArgumentType.string())
                                .suggests(PRESET_SUGGESTIONS)
                                .executes(FpgPresetCommand::setPreset))
                        .executes(context -> {
                            val currentPreset = configFacade().getCurrentPreset().getCode();
                            context.getSource()
                                    .sendSuccess(() -> Component.translatable("fpg.command.preset.current", currentPreset), false);
                            return 1;
                        })
                ).executes(context -> {
                    val helpMessage = Component.translatable("fpg.command.help", String.join("\n", ALL_ARGUMENTS))
                            .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/help")));
                    context.getSource().sendSuccess(() -> helpMessage, false);
                    return 1;
                });

        dispatcher.register(command);
    }

    private static int setPreset(CommandContext<CommandSourceStack> context) {
        val preset = StringArgumentType.getString(context, PRESET_ARGUMENT);
        try {
            if (configFacade().setCurrentPreset(preset)) {
                context.getSource()
                        .sendSuccess(() -> Component.translatable("fpg.command.preset.applied", preset).withStyle(GREEN), false);
                configFacade().save();
                return 1;
            } else {
                val allPresets = configFacade().getAvailablePresetCodes().stream().collect(Collectors.joining("\n"));
                context.getSource()
                        .sendFailure(Component.translatable("fpg.command.preset.notFound", preset, allPresets).withStyle(RED));
                return 0;
            }
        } catch (Exception e) {
            context.getSource()
                    .sendFailure(Component.translatable("fpg.command.preset.exception", e.getMessage()).withStyle(RED));
            return 0;
        }
    }
}
