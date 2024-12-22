package com.naocraftlab.foggypalegarden.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.naocraftlab.foggypalegarden.command.FpgCommand;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

@EventBusSubscriber
public final class FpgPresetCommand implements FpgCommand {

    private static final SuggestionProvider<CommandSourceStack> PRESET_SUGGESTIONS = (context, builder) -> {
        val presets = configFacade().getAvailablePresetCodes();
        return net.minecraft.commands.SharedSuggestionProvider.suggest(presets, builder);
    };

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        val command = Commands.literal(BASE_COMMAND)
                .then(Commands.literal(PRESET_ARGUMENT)
                        .then(Commands.argument(PRESET_ARGUMENT, StringArgumentType.string())
                                .suggests(PRESET_SUGGESTIONS)
                                .executes(FpgPresetCommand::setPreset))
                        .executes(context -> {
                            val currentPreset = configFacade().getCurrentPreset().getCode();
                            context.getSource().sendSuccess(
                                    () -> Component.translatable("fpg.command.preset.current", currentPreset),
                                    false);
                            return 1;
                        })
                ).executes(context -> {
                    context.getSource().sendSuccess(
                            () -> Component.translatable("fpg.command.help", String.join("\n", ALL_ARGUMENTS)),
                            false);
                    return 1;
                });

        dispatcher.register(command);
    }

    private static int setPreset(CommandContext<CommandSourceStack> context) {
        val preset = StringArgumentType.getString(context, PRESET_ARGUMENT);
        try {
            if (configFacade().setCurrentPreset(preset)) {
                context.getSource().sendSuccess(
                        () -> Component.translatable("fpg.command.preset.applied", preset).withStyle(style -> style.withColor(GREEN)),
                        false
                );
                configFacade().save();
                return 1;
            } else {
                val allPresets = String.join("\n", configFacade().getAvailablePresetCodes());
                context.getSource().sendFailure(
                        Component.translatable("fpg.command.preset.notFound", preset, allPresets).withStyle(style -> style.withColor(RED))
                );
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.translatable("fpg.command.preset.exception", e.getMessage()).withStyle(style -> style.withColor(RED))
            );
            return 0;
        }
    }
}
