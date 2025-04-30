package com.naocraftlab.foggypalegarden.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.naocraftlab.foggypalegarden.converter.GameTypeConverter;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static com.naocraftlab.foggypalegarden.converter.GameTypeConverter.toDomainGameType;
import static java.util.stream.Collectors.joining;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;

public class FpgNoFogGameModeCommand extends AbstractCommand {

    private static final SuggestionProvider<CommandSourceStack> GAME_MODE_SUGGESTIONS =
            (context, builder) -> suggest(Stream.of(GameType.values()).map(Enum::name), builder);

    public static final LiteralArgumentBuilder<CommandSourceStack> INSTANCE = Commands.literal(BASE_COMMAND).then(
            Commands.literal(NO_FOG_GAME_MODE_COMMAND).executes(FpgNoFogGameModeCommand::listNoFogGameModes).then(
                    Commands.argument(NO_FOG_GAME_MODE_COMMAND, string())
                            .suggests(GAME_MODE_SUGGESTIONS)
                            .executes(FpgNoFogGameModeCommand::toggleFogForGameMode)
            )
    );

    private static int toggleFogForGameMode(CommandContext<CommandSourceStack> context) {
        val argumentValue = StringArgumentType.getString(context, NO_FOG_GAME_MODE_COMMAND);
        try {
            val gameMode = GameType.valueOf(argumentValue);
            val gameModeName = gameMode.getShortDisplayName().getString();
            if (configFacade().toggleNoFogGameMode(toDomainGameType(gameMode))) {
                context.getSource().sendSuccess(
                        () -> Component.translatable("fpg.command.noFogGameMode.off", gameModeName).withStyle(GRAY),
                        false
                );
            } else {
                context.getSource().sendSuccess(
                        () -> Component.translatable("fpg.command.noFogGameMode.on", gameModeName).withStyle(GREEN),
                        false
                );
            }
            configFacade().save();
            return 1;
        } catch (Exception e) {
            val gameModes = Stream.of(GameType.values()).map(Enum::name).collect(joining("\n"));
            context.getSource().sendFailure(Component.translatable("fpg.command.noFogGameMode.notFound", argumentValue, gameModes));
            return 0;
        }
    }

    private static int listNoFogGameModes(CommandContext<CommandSourceStack> context) {
        if (!configFacade().noFogGameModes().isEmpty()) {
            val noFogGameModes = configFacade().noFogGameModes().stream()
                    .map(GameTypeConverter::toGameType)
                    .map(GameType::getShortDisplayName)
                    .map(Component::getString)
                    .collect(joining("\n"));
            context.getSource().sendSuccess(() -> Component.translatable("fpg.command.noFogGameMode.list", noFogGameModes), false);
        } else {
            context.getSource().sendSuccess(
                    () -> Component.translatable("fpg.command.noFogGameMode.listEmpty").withStyle(GREEN),
                    false
            );
        }
        return 1;
    }
}