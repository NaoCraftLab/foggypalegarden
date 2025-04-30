package com.naocraftlab.foggypalegarden.neoforge;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.MOD_ID;
import static com.naocraftlab.foggypalegarden.util.ReflectUtil.buildScreen;
import static com.naocraftlab.foggypalegarden.util.ReflectUtil.isClassAvailable;
import static net.neoforged.api.distmarker.Dist.CLIENT;
import static net.neoforged.fml.common.EventBusSubscriber.Bus.MOD;

@OnlyIn(CLIENT)
@EventBusSubscriber(modid = MOD_ID, bus = MOD, value = CLIENT)
public final class FoggyPaleGardenClientMod {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        registerConfigScreenFactory();
        LOGGER.info("Foggy Pale Garden client setup complete");
    }

    private static void registerConfigScreenFactory() {
        if (!isClassAvailable("com.naocraftlab.foggypalegarden.clothconfig.ClothConfigScreen")) {
            return;
        }
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (modContainer, parent) -> {
                    if (isModLoaded("cloth_config")) {
                        return buildScreen("com.naocraftlab.foggypalegarden.clothconfig.ClothConfigScreen", parent);
                    } else {
                        return buildScreen("com.naocraftlab.foggypalegarden.clothconfig.NoClothConfigScreen", parent);
                    }
                }
        );
    }

    private static boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getMods()
                .stream()
                .map(ModInfo::getModId)
                .anyMatch(id -> id.equals(modId));
    }
}
