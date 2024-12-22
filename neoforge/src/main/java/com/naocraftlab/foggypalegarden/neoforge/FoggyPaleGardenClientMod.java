package com.naocraftlab.foggypalegarden.neoforge;

import com.mojang.logging.LogUtils;
import com.naocraftlab.foggypalegarden.gui.ClothConfigScreen;
import com.naocraftlab.foggypalegarden.gui.NoClothConfigScreen;
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
import static net.neoforged.api.distmarker.Dist.CLIENT;
import static net.neoforged.fml.common.EventBusSubscriber.Bus.MOD;

@OnlyIn(CLIENT)
@EventBusSubscriber(modid = MOD_ID, bus = MOD, value = CLIENT)
public final class FoggyPaleGardenClientMod {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (modContainer, parent) -> {
                    if (isModLoaded("cloth_config")) {
                        return ClothConfigScreen.of(parent);
                    } else {
                        return NoClothConfigScreen.of(parent);
                    }
                }
        );
        LOGGER.info("Foggy Pale Garden client setup complete");
    }

    private static boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getMods()
                .stream()
                .map(ModInfo::getModId)
                .anyMatch(id -> id.equals(modId));
    }
}
