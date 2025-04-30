package com.naocraftlab.foggypalegarden.modmenu;

import com.naocraftlab.foggypalegarden.clothconfig.ClothConfigScreen;
import com.naocraftlab.foggypalegarden.clothconfig.NoClothConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

public final class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return parent -> {
            if (isClothConfigPresent()) {
                return ClothConfigScreen.of(parent);
            } else {
                return NoClothConfigScreen.of(parent);
            }
        };
    }

    private static boolean isClothConfigPresent() {
        return FabricLoader.getInstance().isModLoaded("cloth-config");
    }
}
