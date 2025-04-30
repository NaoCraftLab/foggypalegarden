package com.naocraftlab.foggypalegarden.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class FpgCommand extends AbstractCommand {

    public static final LiteralArgumentBuilder<CommandSourceStack> INSTANCE = Commands.literal(BASE_COMMAND)
            .executes(FpgCommand::executeBaseCommand);

    private static int executeBaseCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable("fpg.command.help", String.join("\n", ALL_COMMANDS)), false);
        return 1;
    }
}
