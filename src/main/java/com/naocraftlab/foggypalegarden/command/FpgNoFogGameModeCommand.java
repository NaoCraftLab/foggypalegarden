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
import net.minecraft.world.GameMode;

import java.util.stream.Stream;

import static com.naocraftlab.foggypalegarden.FoggyPaleGardenClientMod.configFacade;
import static java.util.stream.Collectors.joining;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

public class FpgNoFogGameModeCommand implements FpgCommand {

    private static final SuggestionProvider<FabricClientCommandSource> GAME_MODE_SUGGESTIONS
            = (context, builder) -> CommandSource.suggestMatching(Stream.of(GameMode.values()).map(Enum::name), builder);

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal(BASE_COMMAND).then(ClientCommandManager.literal(NO_FOG_GAME_MODE_ARGUMENT)
                        .executes(FpgNoFogGameModeCommand::listNoFogGameModes)
                        .then(ClientCommandManager.argument(NO_FOG_GAME_MODE_ARGUMENT, StringArgumentType.string())
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
            if (configFacade().toggleNoFogGameMode(gameMode)) {
                context.getSource().sendError(Text.translatable("fpg.command.noFogGameMode.off", gameMode).formatted(YELLOW));
            } else {
                context.getSource().sendFeedback(Text.translatable("fpg.command.noFogGameMode.on", gameMode).formatted(GREEN));
            }
            configFacade().save();
            return 1;
        } catch (Exception e) {
            val gameModes = Stream.of(GameMode.values()).map(Enum::name).collect(joining("\n"));
            context.getSource().sendError(
                    Text.translatable("fpg.command.noFogGameMode.notFound", argumentValue, gameModes).formatted(RED)
            );
            return 0;
        }
    }

    private static int listNoFogGameModes(CommandContext<FabricClientCommandSource> context) {
        if (!configFacade().noFogGameModes().isEmpty()) {
            val noFogGameModes = configFacade().noFogGameModes().stream()
                    .map(GameMode::name)
                    .collect(joining("\n"));
            context.getSource().sendFeedback(Text.translatable("fpg.command.noFogGameMode.list", noFogGameModes).formatted(GREEN));
        } else {
            context.getSource().sendFeedback(Text.translatable("fpg.command.noFogGameMode.listEmpty").formatted(GREEN));
        }
        return 1;
    }
}