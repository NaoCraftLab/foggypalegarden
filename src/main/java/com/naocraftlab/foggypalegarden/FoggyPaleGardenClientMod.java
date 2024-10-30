package com.naocraftlab.foggypalegarden;

import com.mojang.brigadier.CommandDispatcher;
import com.naocraftlab.foggypalegarden.command.FpgNoFogGameModeCommand;
import com.naocraftlab.foggypalegarden.command.FpgPresetCommand;
import com.naocraftlab.foggypalegarden.command.FpgReloadConfigCommand;
import com.naocraftlab.foggypalegarden.config.ConfigFacade;
import com.naocraftlab.foggypalegarden.config.ConfigMigrator;
import com.naocraftlab.foggypalegarden.domain.service.FogService;
import lombok.val;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

// FIXME v2.8.0
// FIXME поддержка модменю
// FIXME перевод новых строк
// FIXME чейнджлог
// FIXME скрин
// FIXME ридми
// FIXME сменить modId и конввертировать старые конфиги
// FIXME обновить описание и скрины на площадках

// FIXME vNEXT
// FIXME баг с яркостью неба при установки фиксированного цвета
// FIXME сборка под неофорж
// FIXME бекпорт на предыдущие версии игры???
public class FoggyPaleGardenClientMod implements ClientModInitializer {

    public static final String MOD_ID = "foggypalegarden";

    private static ConfigFacade configFacade;


    @Override
    public void onInitializeClient() {
        initConfigFacade();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerCommands(dispatcher);
        });
    }

    @NotNull
    public static ConfigFacade configFacade() {
        if (configFacade == null) {
            initConfigFacade();
        }
        return configFacade;
    }


    private static void initConfigFacade() {
        val configFilePtah = FabricLoader.getInstance().getConfigDir().resolve(Paths.get(MOD_ID + ".json"));
        val presetDirectoryPath = FabricLoader.getInstance().getConfigDir().resolve(Paths.get(MOD_ID.replaceAll("-", "")));
        configFacade = new ConfigFacade(configFilePtah, presetDirectoryPath, new ConfigMigrator(presetDirectoryPath));
        configFacade.registerCurrentPresetListener(FogService::onCurrentPresetChange);
        configFacade.load();
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        FpgPresetCommand.register(dispatcher);
        FpgReloadConfigCommand.register(dispatcher);
        FpgNoFogGameModeCommand.register(dispatcher);
    }
}
