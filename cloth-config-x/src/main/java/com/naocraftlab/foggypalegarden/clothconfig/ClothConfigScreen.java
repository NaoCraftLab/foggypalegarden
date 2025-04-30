package com.naocraftlab.foggypalegarden.clothconfig;

import com.naocraftlab.foggypalegarden.domain.model.GameType;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.List;

import static com.naocraftlab.foggypalegarden.config.ConfigFacade.configFacade;
import static com.naocraftlab.foggypalegarden.config.presetsource.PresetSourceEmbedded.DEFAULT_PRESET_CODE;
import static com.naocraftlab.foggypalegarden.converter.GameTypeConverter.toDomainGameType;
import static net.minecraft.world.level.GameType.ADVENTURE;
import static net.minecraft.world.level.GameType.CREATIVE;
import static net.minecraft.world.level.GameType.SPECTATOR;
import static net.minecraft.world.level.GameType.SURVIVAL;

@UtilityClass
public class ClothConfigScreen {

    public static Screen of(Screen parent) {
        configFacade().load();

        val builder = ConfigBuilder.create().setTitle(Component.translatable("fpg.settings.title")).setParentScreen(parent);
        val entryBuilder = builder.entryBuilder();

        val generalCategory = builder.getOrCreateCategory(Component.translatable("fpg.settings.category.general.title"));

        val presetEntry = entryBuilder.startSelector(
                        Component.translatable("fpg.settings.currentPreset.title"),
                        configFacade().getAvailablePresetCodes().toArray(),
                        configFacade().getCurrentPreset().getCode()
                )
                // TODO new tooltip
                // .setTooltip(
                //         Component.translatable("fpg.settings.currentPreset.tooltip", configFacade().presetDirectoryPath().normalize().toString())
                // )
                .setDefaultValue(DEFAULT_PRESET_CODE)
                .build();
        generalCategory.addEntry(presetEntry);

        val creativeEntry = entryBuilder.startBooleanToggle(
                Component.translatable("selectWorld.gameMode.creative"),
                configFacade().isNoFogGameMode(toDomainGameType(CREATIVE))
        ).setDefaultValue(false).build();
        val survivalEntry = entryBuilder.startBooleanToggle(
                Component.translatable("selectWorld.gameMode.survival"),
                configFacade().isNoFogGameMode(toDomainGameType(SURVIVAL))
        ).setDefaultValue(false).build();
        val adventureEntry = entryBuilder.startBooleanToggle(
                Component.translatable("selectWorld.gameMode.adventure"),
                configFacade().isNoFogGameMode(toDomainGameType(ADVENTURE))
        ).setDefaultValue(false).build();
        val spectatorEntry = entryBuilder.startBooleanToggle(
                Component.translatable("selectWorld.gameMode.spectator"),
                configFacade().isNoFogGameMode(toDomainGameType(SPECTATOR))
        ).setDefaultValue(false).build();
        val noFogGameModeCategory = entryBuilder.startSubCategory(
                Component.translatable("fpg.settings.subCategory.noFogGameMode.title"),
                List.of(creativeEntry, survivalEntry, adventureEntry, spectatorEntry)
        ).setTooltip(Component.translatable("fpg.settings.subCategory.noFogGameMode.tooltip"))
                .setExpanded(true)
                .build();
        generalCategory.addEntry(noFogGameModeCategory);

        builder.setSavingRunnable(() -> {
            configFacade().setCurrentPreset((String) presetEntry.getValue());

            val noFogGameModes = new HashSet<GameType>();
            if (creativeEntry.getValue()) {
                noFogGameModes.add(GameType.CREATIVE);
            }
            if (survivalEntry.getValue()) {
                noFogGameModes.add(GameType.SURVIVAL);
            }
            if (adventureEntry.getValue()) {
                noFogGameModes.add(GameType.ADVENTURE);
            }
            if (spectatorEntry.getValue()) {
                noFogGameModes.add(GameType.SPECTATOR);
            }
            configFacade().noFogGameModes(noFogGameModes);

            configFacade().save();
        });

        return builder.build();
    }
}
