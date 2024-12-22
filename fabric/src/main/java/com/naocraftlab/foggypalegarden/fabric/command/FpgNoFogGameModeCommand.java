package com.naocraftlab.foggypalegarden.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.naocraftlab.foggypalegarden.command.FpgCommand;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

import java.util.stream.Stream;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static java.util.stream.Collectors.joining;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;

public class FpgNoFogGameModeCommand implements FpgCommand {

    private static final SuggestionProvider<CommandSourceStack> GAME_MODE_SUGGESTIONS =
            (context, builder) -> suggest(Stream.of(GameType.values()).map(Enum::name), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(BASE_COMMAND)
                        .then(Commands.literal(NO_FOG_GAME_MODE_ARGUMENT)
                                .executes(FpgNoFogGameModeCommand::listNoFogGameModes)
                                .then(
                                        Commands.argument(NO_FOG_GAME_MODE_ARGUMENT, StringArgumentType.string())
                                                .suggests(GAME_MODE_SUGGESTIONS)
                                                .executes(FpgNoFogGameModeCommand::toggleFogForGameMode)
                                )
                        )
        );
    }

    private static int toggleFogForGameMode(CommandContext<CommandSourceStack> context) {
        val argumentValue = StringArgumentType.getString(context, NO_FOG_GAME_MODE_ARGUMENT);
        try {
            val gameMode = GameType.valueOf(argumentValue);
            if (configFacade().toggleNoFogGameMode(gameMode)) {
                context.getSource().sendFailure(Component.translatable("fpg.command.noFogGameMode.off", gameMode));
            } else {
                context.getSource().sendSuccess(() -> Component.translatable("fpg.command.noFogGameMode.on", gameMode), false);
            }
            configFacade().save();
            return 1;
        } catch (Exception e) {
            val gameModes = Stream.of(GameType.values()).map(Enum::name).collect(joining("\n"));
            context.getSource().sendFailure(
                    Component.translatable("fpg.command.noFogGameMode.notFound", argumentValue, gameModes)
            );
            return 0;
        }
    }

    private static int listNoFogGameModes(CommandContext<CommandSourceStack> context) {
        if (!configFacade().noFogGameModes().isEmpty()) {
            val noFogGameModes = configFacade().noFogGameModes().stream()
                    .map(GameType::name)
                    .collect(joining("\n"));
            context.getSource()
                    .sendSuccess(() -> Component.translatable("fpg.command.noFogGameMode.list", noFogGameModes), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("fpg.command.noFogGameMode.listEmpty"), false);
        }
        return 1;
    }
}