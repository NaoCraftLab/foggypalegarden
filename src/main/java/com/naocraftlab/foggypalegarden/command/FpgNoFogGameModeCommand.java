package com.naocraftlab.foggypalegarden.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.naocraftlab.foggypalegarden.config.ConfigManager;
import lombok.val;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

public class FpgNoFogGameModeCommand implements FpgCommand {

    private static final SuggestionProvider<FabricClientCommandSource> GAME_MODE_SUGGESTIONS
            = (context, builder) -> CommandSource.suggestMatching(Stream.of(GameMode.values()).map(Enum::name), builder);

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal(BASE_COMMAND).then(ClientCommandManager.literal(NO_FOG_GAME_MODE_ARGUMENT).then(
                        ClientCommandManager.argument(NO_FOG_GAME_MODE_ARGUMENT, StringArgumentType.string())
                                .suggests(GAME_MODE_SUGGESTIONS)
                                .executes(FpgNoFogGameModeCommand::toggleFogForGameMode)
                        )
                )
        );
    }

    private static int toggleFogForGameMode(CommandContext<FabricClientCommandSource> context) {
        val argumentValue = StringArgumentType.getString(context, NO_FOG_GAME_MODE_ARGUMENT);
        try {
            val gameMode = GameMode.valueOf(argumentValue);
            val currentConfig = ConfigManager.currentConfig();
            if (currentConfig.getNoFogGameModes().contains(gameMode)) {
                val noFogGameModes = currentConfig.getNoFogGameModes().stream().filter(gm -> gm != gameMode).collect(toSet());
                ConfigManager.saveConfig(currentConfig.withNoFogGameModes(noFogGameModes));
                context.getSource().sendFeedback(Text.translatable("fpg.command.noFogGameMode.on", gameMode).formatted(GREEN));
            } else {
                val noFogGameModes = new HashSet<>(currentConfig.getNoFogGameModes());
                noFogGameModes.add(gameMode);
                ConfigManager.saveConfig(currentConfig.withNoFogGameModes(noFogGameModes));
                context.getSource().sendError(Text.translatable("fpg.command.noFogGameMode.off", gameMode).formatted(YELLOW));
            }
            return 1;
        } catch (Exception e) {
            val gameModes = Stream.of(GameMode.values()).map(Enum::name).collect(joining("\n"));
            context.getSource().sendError(
                    Text.translatable("fpg.command.noFogGameMode.notFound", argumentValue, gameModes).formatted(RED)
            );
            return 0;
        }
    }
}
