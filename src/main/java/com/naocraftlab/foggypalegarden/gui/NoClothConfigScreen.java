package com.naocraftlab.foggypalegarden.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;

import java.net.URI;

import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Util.getOperatingSystem;

@UtilityClass
public class NoClothConfigScreen {

    private static final URI CLOTH_CONFIG_MODRINTH = URI.create("https://modrinth.com/mod/cloth-config");

    public static Screen of(Screen parent) {
        return new ConfirmLinkScreen(onClick(parent), translatable("fpg.settings.warning.noClothConfig"), CLOTH_CONFIG_MODRINTH, true);
    }

    private static BooleanConsumer onClick(Screen parent) {
        return confirmed -> {
            val client = MinecraftClient.getInstance();
            if (confirmed) {
                getOperatingSystem().open(CLOTH_CONFIG_MODRINTH);
            }
            client.setScreen(parent);
        };
    }
}
