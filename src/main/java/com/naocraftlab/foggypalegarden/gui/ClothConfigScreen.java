package com.naocraftlab.foggypalegarden.gui;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.List;

import static com.naocraftlab.foggypalegarden.FoggyPaleGardenClientMod.configFacade;
import static com.naocraftlab.foggypalegarden.config.ConfigMigrator.DEFAULT_CONFIG;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.world.GameMode.ADVENTURE;
import static net.minecraft.world.GameMode.CREATIVE;
import static net.minecraft.world.GameMode.SPECTATOR;
import static net.minecraft.world.GameMode.SURVIVAL;

@UtilityClass
public class ClothConfigScreen {

    public static Screen of(Screen parent) {
        configFacade().load();

        val builder = ConfigBuilder.create().setTitle(translatable("fpg.settings.title")).setParentScreen(parent);
        val entryBuilder = builder.entryBuilder();

        val generalCategory = builder.getOrCreateCategory(translatable("fpg.settings.category.general.title"));

        val presetEntry = entryBuilder.startSelector(
                        translatable("fpg.settings.currentPreset.title"),
                        configFacade().getAvailablePresetCodes().toArray(),
                        configFacade().getCurrentPreset().getCode()
                ).setTooltip(
                        translatable("fpg.settings.currentPreset.tooltip", configFacade().presetDirectoryPath().normalize().toString())
                ).setDefaultValue(DEFAULT_CONFIG.getPreset())
                .build();
        generalCategory.addEntry(presetEntry);

        val creativeEntry = entryBuilder.startBooleanToggle(
                translatable("selectWorld.gameMode.creative"),
                configFacade().isNoFogGameMode(CREATIVE)
        ).setDefaultValue(false).build();
        val survivalEntry = entryBuilder.startBooleanToggle(
                translatable("selectWorld.gameMode.survival"),
                configFacade().isNoFogGameMode(SURVIVAL)
        ).setDefaultValue(false).build();
        val adventureEntry = entryBuilder.startBooleanToggle(
                translatable("selectWorld.gameMode.adventure"),
                configFacade().isNoFogGameMode(ADVENTURE)
        ).setDefaultValue(false).build();
        val spectatorEntry = entryBuilder.startBooleanToggle(
                translatable("selectWorld.gameMode.spectator"),
                configFacade().isNoFogGameMode(SPECTATOR)
        ).setDefaultValue(false).build();
        val noFogGameModeCategory = entryBuilder.startSubCategory(
                translatable("fpg.settings.subCategory.noFogGameMode.title"),
                List.of(creativeEntry, survivalEntry, adventureEntry, spectatorEntry)
        ).setTooltip(translatable("fpg.settings.subCategory.noFogGameMode.tooltip"))
                .setExpanded(true)
                .build();
        generalCategory.addEntry(noFogGameModeCategory);

        builder.setSavingRunnable(() -> {
            configFacade().setCurrentPreset((String) presetEntry.getValue());

            val noFogGameModes = new HashSet<GameMode>();
            if (creativeEntry.getValue()) {
                noFogGameModes.add(CREATIVE);
            }
            if (survivalEntry.getValue()) {
                noFogGameModes.add(SURVIVAL);
            }
            if (adventureEntry.getValue()) {
                noFogGameModes.add(ADVENTURE);
            }
            if (spectatorEntry.getValue()) {
                noFogGameModes.add(SPECTATOR);
            }
            configFacade().noFogGameModes(noFogGameModes);

            configFacade().save();
        });

        return builder.build();
    }
}
