package com.naocraftlab.foggypalegarden.chat;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.MOD_NAME;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

@UtilityClass
public class MessageSender {

    public static final Supplier<Component> DISABLE_FOG_OCCLUSION = () ->
            translatable("fpg.message.disableFogOcclusion", MOD_NAME)
                    .append(literal(": "))
                    .append(translatable("options.video").withStyle(style -> style.withColor(YELLOW)))
                    .append(literal(" - ").withStyle(style -> style.withColor(YELLOW)))
                    .append(translatable("sodium.options.pages.performance").withStyle(style -> style.withColor(YELLOW)))
                    .append(literal(" - ").withStyle(style -> style.withColor(YELLOW)))
                    .append(translatable("sodium.options.use_fog_occlusion.name").withStyle(style -> style.withColor(YELLOW)));

    public static void sendToClientChat(Supplier<Component> messageSupplier) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.getChat().addMessage(messageSupplier.get()));
    }
}
