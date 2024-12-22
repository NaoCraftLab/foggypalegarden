package com.naocraftlab.foggypalegarden.neoforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.naocraftlab.foggypalegarden.command.FpgCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.stream.Stream;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static java.util.stream.Collectors.joining;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

@EventBusSubscriber
public final class FpgNoFogGameModeCommand implements FpgCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal(BASE_COMMAND)
                        .then(Commands.literal(NO_FOG_GAME_MODE_ARGUMENT)
                                .executes(FpgNoFogGameModeCommand::listNoFogGameModes)
                                .then(Commands.argument(NO_FOG_GAME_MODE_ARGUMENT_FIRST_ARG, StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                Stream.of(GameType.values()).map(Enum::name), builder
                                        ))
                                        .executes(FpgNoFogGameModeCommand::toggleFogForGameMode)
                                )
                        )
        );
    }

    private static int toggleFogForGameMode(CommandContext<CommandSourceStack> context) {
        String argumentValue = StringArgumentType.getString(context, NO_FOG_GAME_MODE_ARGUMENT_FIRST_ARG);
        try {
            GameType gameMode = GameType.valueOf(argumentValue.toUpperCase());
            if (configFacade().toggleNoFogGameMode(gameMode)) {
                context.getSource().sendFailure(
                        Component.translatable("fpg.command.noFogGameMode.off", gameMode.name())
                                .withStyle(style -> style.withColor(GOLD))
                );
            } else {
                context.getSource().sendSuccess(
                        () -> Component.translatable("fpg.command.noFogGameMode.on", gameMode.name())
                                .withStyle(style -> style.withColor(GREEN)),
                        false
                );
            }
            configFacade().save();
            return 1;
        } catch (Exception e) {
            String gameModes = Stream.of(GameType.values())
                    .map(Enum::name)
                    .collect(joining("\n"));
            context.getSource().sendFailure(
                    Component.translatable("fpg.command.noFogGameMode.notFound", argumentValue, gameModes)
                            .withStyle(style -> style.withColor(RED))
            );
            return 0;
        }
    }

    private static int listNoFogGameModes(CommandContext<CommandSourceStack> context) {
        if (!configFacade().noFogGameModes().isEmpty()) {
            String noFogGameModes = configFacade().noFogGameModes().stream()
                    .map(GameType::name)
                    .collect(joining("\n"));
            context.getSource().sendSuccess(
                    () -> Component.translatable("fpg.command.noFogGameMode.list", noFogGameModes)
                            .withStyle(style -> style.withColor(GREEN)),
                    false
            );
        } else {
            context.getSource().sendSuccess(
                    () -> Component.translatable("fpg.command.noFogGameMode.listEmpty")
                            .withStyle(style -> style.withColor(GREEN)),
                    false
            );
        }
        return 1;
    }
}
