package com.naocraftlab.foggypalegarden.converter;

import com.naocraftlab.foggypalegarden.domain.model.GameType;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenInternalException;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

// TODO create other converters
@UtilityClass
public class GameTypeConverter {

    @NotNull
    public static GameType toDomainGameType(@NotNull net.minecraft.world.level.GameType gameType) {
        try {
            return GameType.valueOf(gameType.name());
        } catch (Exception e) {
            throw new FoggyPaleGardenInternalException("Unknown game type: " + gameType.name(), e);
        }
    }

    @NotNull
    public static net.minecraft.world.level.GameType toGameType(@NotNull GameType gameType) {
        try {
            return net.minecraft.world.level.GameType.valueOf(gameType.name());
        } catch (Exception e) {
            throw new FoggyPaleGardenInternalException("Unknown game type: " + gameType.name(), e);
        }
    }
}
