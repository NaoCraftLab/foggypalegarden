package com.naocraftlab.foggypalegarden.forge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.MOD_ID;
import static com.naocraftlab.foggypalegarden.util.ReflectUtil.buildScreen;
import static net.minecraftforge.api.distmarker.Dist.CLIENT;

@OnlyIn(CLIENT)
@Mod(value = MOD_ID)
public final class FoggyPaleGardenClientMod {

    private static final Logger LOGGER = LogUtils.getLogger();

    public FoggyPaleGardenClientMod() {
        registerConfigScreenFactory();
        LOGGER.info("Foggy Pale Garden client setup complete.");
    }

    private static void registerConfigScreenFactory() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenFactory.class,
                () -> new ConfigScreenFactory((mc, screen) -> {
                    if (ModList.get().isLoaded("cloth_config")) {
                        return buildScreen("com.naocraftlab.foggypalegarden.clothconfig.ClothConfigScreen", screen);
                    } else {
                        return buildScreen("com.naocraftlab.foggypalegarden.clothconfig.NoClothConfigScreen", screen);
                    }
                })
        );
    }
}
