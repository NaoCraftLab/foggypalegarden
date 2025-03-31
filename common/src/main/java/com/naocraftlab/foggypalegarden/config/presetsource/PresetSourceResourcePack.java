package com.naocraftlab.foggypalegarden.config.presetsource;

import com.naocraftlab.foggypalegarden.config.preset.FogPreset;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.GSON;
import static com.naocraftlab.foggypalegarden.FoggyPaleGarden.MOD_ID;
import static com.naocraftlab.foggypalegarden.config.presetsource.PresetSource.PresetSourceType.RESOURCE_PACK;

@RequiredArgsConstructor
public class PresetSourceResourcePack<T extends FogPreset> implements PresetSource<T> {

    private static final String PRESET_RESOURCE_NAME = "preset.json";

    private final int presetVersion;

    private final Class<T> presetType;

    @Override
    public PresetSourceType type() {
        return RESOURCE_PACK;
    }

    @Override
    public List<PresetBox<T>> load() {
        val presetBoxes = new ArrayList<PresetBox<T>>();
        val resourceManager = Minecraft.getInstance().getResourceManager();
        val resources = resourceManager.getResourceStack(ResourceLocation.tryBuild(MOD_ID, PRESET_RESOURCE_NAME));
        for (val resource : resources) {
            try (val stream = resource.open()) {
                val json = new String(stream.readAllBytes());
                val preset = GSON.fromJson(json, FogPreset.class);
                if (preset.getVersion() == presetVersion) {
                    val presetV3 = GSON.fromJson(json, presetType);
                    try (val source = resource.source()) {
                        val presetBox = PresetBox.<T>builder()
                                .sourceType(type())
                                .code(presetV3.getCode())
                                .path(source.packId().replaceAll("^file/", "") + ":assets/" + MOD_ID + "/" + PRESET_RESOURCE_NAME)
                                .preset(presetV3)
                                .build();
                        presetBoxes.add(presetBox);
                    }
                }
            } catch (Exception e) {
                // TODO logs
                e.printStackTrace();
            }
        }
        return presetBoxes;
    }

    @Override
    public void save(Collection<PresetBox<T>> presets) {
        // not supported
    }
}

//1.20.1
//read
//tryBuild
//tryParse

//1.21.4
//read
//tryBuild
//tryParse
