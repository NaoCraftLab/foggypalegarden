package com.naocraftlab.foggypalegarden.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.val;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import static com.naocraftlab.foggypalegarden.FoggyPaleGardenClientMod.configFacade;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class FpgPresetCommand implements FpgCommand {

    private static final SuggestionProvider<FabricClientCommandSource> PRESET_SUGGESTIONS = (context, builder) -> {
        val presets = configFacade().getAvailablePresetCodes();
        return CommandSource.suggestMatching(presets, builder);
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        val command = ClientCommandManager.literal(BASE_COMMAND)
                .then(ClientCommandManager.literal(PRESET_ARGUMENT)
                        .then(ClientCommandManager.argument(PRESET_ARGUMENT, StringArgumentType.string())
                                .suggests(PRESET_SUGGESTIONS)
                                .executes(FpgPresetCommand::setPreset))
                        .executes(context -> {
                            val currentPreset = configFacade().getCurrentPreset().getCode();
                            context.getSource().sendFeedback(Text.translatable("fpg.command.preset.current", currentPreset));
                            return 1;
                        })
                ).executes(context -> {
                    context.getSource().sendFeedback(Text.translatable("fpg.command.help", String.join("\n", ALL_ARGUMENTS)));
                    return 1;
                });

        dispatcher.register(command);
    }

    private static int setPreset(CommandContext<FabricClientCommandSource> context) {
        val preset = StringArgumentType.getString(context, PRESET_ARGUMENT);
        try {
            if (configFacade().setCurrentPreset(preset)) {
                context.getSource().sendFeedback(Text.translatable("fpg.command.preset.applied", preset).formatted(GREEN));
                configFacade().save();
                return 1;
            } else {
                val allPresets = String.join("\n", configFacade().getAvailablePresetCodes());
                context.getSource().sendError(Text.translatable("fpg.command.preset.notFound", preset, allPresets).formatted(RED));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendError(Text.translatable("fpg.command.preset.exception", e.getMessage()).formatted(RED));
            return 0;
        }
    }
}
