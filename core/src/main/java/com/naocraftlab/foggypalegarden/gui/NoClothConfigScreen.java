package com.naocraftlab.foggypalegarden.gui;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;

import java.net.URI;

import static net.minecraft.network.chat.Component.translatable;

public final class NoClothConfigScreen {

    private static final URI CLOTH_CONFIG_MODRINTH = URI.create("https://modrinth.com/mod/cloth-config");

    public static Screen of(Screen parent) {
        return new ConfirmLinkScreen(
                confirmed -> onClick(confirmed, parent),
                translatable("fpg.settings.warning.noClothConfig"),
                CLOTH_CONFIG_MODRINTH.toString(),
                true);
    }

    private static void onClick(boolean confirmed, Screen parent) {
        if (confirmed) {
            Util.getPlatform().openUri(CLOTH_CONFIG_MODRINTH);
        }
        Minecraft.getInstance().setScreen(parent);
    }
}
