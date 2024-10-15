package com.naocraftlab.foggypalegarden.config.preset;

import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenConfigurationException;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;
import net.minecraft.client.render.FogShape;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
public final class FogPresetV3 extends FogPreset {

    /**
     * Preset code.
     */
    @NotNull
    private final String code;

    /**
     * Bindings.
     */
    @NotNull
    private final List<Binding> bindings;

    public FogPresetV3(@NotNull String code, @NotNull List<Binding> bindings) {
        super(3);
        this.code = code;
        this.bindings = bindings;
    }

    @Builder
    public record Binding(
            Condition condition,
            Float startDistance,
            Float endDistance,
            Float opacity,
            Float encapsulationSpeed,
            Brightness brightness,
            Color color,
            FogShape shape
    ) {

        @Builder
        public record Condition(
                Set<String> dimensionIn,
                Set<String> biomeIdIn,
                Temperature biomeTemperature,
                Set<Difficulty> difficultyIn,
                Set<Weather> weatherIn,
                TimePeriod timeIn,
                SkyLightLevel skyLightLevel,
                Height height,
                SurfaceHeight surfaceHeight,
                List<Condition> and,
                List<Condition> or,
                Condition not
        ) {

            public enum Weather {
                CLEAR,
                RAIN,
                THUNDER
            }

            @Builder
            public record TimePeriod(
                    Long start,
                    Long end
            ) {

                public void validate() {
                    if (start == null || end == null) {
                        throw new FoggyPaleGardenConfigurationException("TimePeriod start and end cannot be null");
                    }
                    if (start < 0 || start >= 24000 || end < 0 || end >= 24000) {
                        throw new FoggyPaleGardenConfigurationException(
                                "TimePeriod start and end must be in the range [0, 24000)"
                        );
                    }
                }
            }

            @Builder
            public record Temperature(
                    Float min,
                    Float max
            ) {

                public void validate() {
                    if (min == null && max == null) {
                        throw new FoggyPaleGardenConfigurationException("Temperature min and max cannot be both null");
                    }
                    if (min != null && max != null && min >= max) {
                        throw new FoggyPaleGardenConfigurationException("Temperature min must be less than max");
                    }
                }
            }

            @Builder
            public record SkyLightLevel(
                    Integer min,
                    Integer max
            ) {

                    public void validate() {
                        if (min == null && max == null) {
                            throw new FoggyPaleGardenConfigurationException("Sky light level min and max cannot be both null");
                        }
                        if (min != null && max != null && min >= max) {
                            throw new FoggyPaleGardenConfigurationException("Sky light level min must be less than max");
                        }
                        if (min != null && (min < 0 || min > 15)) {
                            throw new FoggyPaleGardenConfigurationException("Sky light level min is out of range [0, 15]");
                        }
                        if (max != null && (max < 0 || max > 15)) {
                            throw new FoggyPaleGardenConfigurationException("Sky light level max is out of range [0, 15]");
                        }
                    }
            }

            @Builder
            public record Height(
                    Double min,
                    Double max
            ) {

                    public void validate() {
                        if (min == null && max == null) {
                            throw new FoggyPaleGardenConfigurationException("Height min and max cannot be both null");
                        }
                        if (min != null && max != null && min >= max) {
                            throw new FoggyPaleGardenConfigurationException("Height min must be less than max");
                        }
                    }
            }

            @Builder
            public record SurfaceHeight(
                    Float min,
                    Float max
            ) {

                    public void validate() {
                        if (min == null && max == null) {
                            throw new FoggyPaleGardenConfigurationException("Surface height min and max cannot be both null");
                        }
                        if (min != null && max != null && min >= max) {
                            throw new FoggyPaleGardenConfigurationException("Surface height min must be less than max");
                        }
                        if (min != null && min < 0.0f) {
                            throw new FoggyPaleGardenConfigurationException("Surface height min is negative");
                        }
                        if (max != null && max < 0.0f) {
                            throw new FoggyPaleGardenConfigurationException("Surface height max is negative");
                        }
                    }
            }

            public void validate() {
                int filledFields = 0;
                if (dimensionIn != null && !dimensionIn.isEmpty()) {
                    filledFields++;
                }
                if (biomeTemperature != null) {
                    filledFields++;
                    biomeTemperature.validate();
                }
                if (biomeIdIn != null && !biomeIdIn.isEmpty()) {
                    filledFields++;
                }
                if (difficultyIn != null && !difficultyIn.isEmpty()) {
                    filledFields++;
                }
                if (weatherIn != null && !weatherIn.isEmpty()) {
                    filledFields++;
                }
                if (timeIn != null) {
                    filledFields++;
                    timeIn.validate();
                }
                if (skyLightLevel != null) {
                    filledFields++;
                    skyLightLevel.validate();
                }
                if (height != null) {
                    filledFields++;
                    height.validate();
                }
                if (surfaceHeight != null) {
                    filledFields++;
                    surfaceHeight.validate();
                }
                if (and != null && !and.isEmpty()) {
                    filledFields++;
                    for (Condition condition : and) {
                        if (condition == null) {
                            throw new FoggyPaleGardenConfigurationException("AND list contains a null condition");
                        }
                        condition.validate();
                    }
                }
                if (or != null && !or.isEmpty()) {
                    filledFields++;
                    for (Condition condition : or) {
                        if (condition == null) {
                            throw new FoggyPaleGardenConfigurationException("OR list contains a null condition");
                        }
                        condition.validate();
                    }
                }
                if (not != null) {
                    filledFields++;
                    not.validate();
                }

                if (filledFields != 1) {
                    throw new FoggyPaleGardenConfigurationException(
                            "In one instance of condition, only one of the fields should be filled"
                    );
                }
            }

            public Predicate<Environment> toPredicate() {
                if (and != null && !and.isEmpty()) {
                    return and.stream()
                            .map(Condition::toPredicate)
                            .reduce(Predicate::and)
                            .orElse(env -> true);
                } else if (or != null && !or.isEmpty()) {
                    return or.stream()
                            .map(Condition::toPredicate)
                            .reduce(Predicate::or)
                            .orElse(env -> false);
                } else if (not != null) {
                    return not.toPredicate().negate();
                } else {
                    Predicate<Environment> predicate = env -> true;
                    if (dimensionIn != null && !dimensionIn.isEmpty()) {
                        predicate = predicate.and(env -> dimensionIn.contains(env.dimension()));
                    }
                    if (biomeIdIn != null && !biomeIdIn.isEmpty()) {
                        predicate = predicate.and(env -> biomeIdIn.contains(env.biome()));
                    }
                    if (biomeTemperature != null) {
                        val min = biomeTemperature.min();
                        val max = biomeTemperature.max();
                        predicate = predicate.and(env -> {
                            val temperature = env.biomeTemperature();
                            if (min != null && max != null) {
                                return temperature >= min && temperature <= max;
                            } else if (min != null) {
                                return temperature >= min;
                            } else {
                                return temperature <= max;
                            }
                        });
                    }
                    if (difficultyIn != null && !difficultyIn.isEmpty()) {
                        predicate = predicate.and(env -> difficultyIn.contains(env.difficulty()));
                    }
                    if (weatherIn != null && !weatherIn.isEmpty()) {
                        predicate = predicate.and(env -> weatherIn.contains(Weather.valueOf(env.weather().name())));
                    }
                    if (timeIn != null) {
                        val start = timeIn.start();
                        val end = timeIn.end();
                        predicate = predicate.and(env -> {
                            val time = env.timeOfDay();
                            if (start <= end) {
                                return time >= start && time <= end;
                            } else {
                                return time >= start || time <= end;
                            }
                        });
                    }
                    if (skyLightLevel != null) {
                        val min = skyLightLevel.min();
                        val max = skyLightLevel.max();
                        predicate = predicate.and(env -> {
                            val light = env.skyLightLevel();
                            if (min != null && max != null) {
                                return light >= min && light <= max;
                            } else if (min != null) {
                                return light >= min;
                            } else {
                                return light <= max;
                            }
                        });
                    }
                    if (height != null) {
                        val min = height.min();
                        val max = height.max();
                        predicate = predicate.and(env -> {
                            val height = env.height();
                            if (min != null && max != null) {
                                return height >= min && height <= max;
                            } else if (min != null) {
                                return height >= min;
                            } else {
                                return height <= max;
                            }
                        });
                    }
                    if (surfaceHeight != null) {
                        val min = surfaceHeight.min();
                        val max = surfaceHeight.max();
                        predicate = predicate.and(env -> {
                            val surfaceHeight = env.heightAboveSurface();
                            if (min != null && max != null) {
                                return surfaceHeight >= min && surfaceHeight <= max;
                            } else if (min != null) {
                                return surfaceHeight >= min;
                            } else {
                                return surfaceHeight <= max;
                            }
                        });
                    }
                    return predicate;
                }
            }
        }

        @Builder
        public record Brightness(
                BrightnessMode mode,
                Float fixedBrightness,
                Float adjustment
        ) {

            public enum BrightnessMode {
                FIXED,
                BY_GAME_FOG
            }

            public void validate() {
                if (mode == BrightnessMode.FIXED && (fixedBrightness == null || fixedBrightness < 0.0f || fixedBrightness > 100.0f)) {
                    throw new FoggyPaleGardenConfigurationException(
                            "Binding brightness fixedBrightness is not defined or out of range [0.0, 1.0]"
                    );
                }
                if (mode == BrightnessMode.BY_GAME_FOG && adjustment != null
                    && (adjustment < -1.0f || adjustment > 1.0f)) {
                    throw new FoggyPaleGardenConfigurationException("Binding brightness adjustment is out of range [-1.0, 1.0]");
                }
            }
        }

        @Builder
        public record Color(
                ColorMode mode,
                String fixedHex
        ) {

            private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]{6}$");

            public enum ColorMode {
                FIXED,
                BY_GAME_FOG
            }

            public void validate() {
                if (mode == ColorMode.FIXED && (fixedHex == null || fixedHex.isBlank() || !HEX_PATTERN.matcher(fixedHex).matches())) {
                    throw new FoggyPaleGardenConfigurationException("Binding fixedHex is not defined or invalid");
                }
            }
        }

        @Override
        public Float startDistance() {
            return startDistance == null ? 0.0f : startDistance;
        }

        @Override
        public Float endDistance() {
            return endDistance == null ? 0.0f : endDistance;
        }

        @Override
        public Float opacity() {
            return opacity == null ? 100.0f : opacity;
        }

        @Override
        public Float encapsulationSpeed() {
            return encapsulationSpeed == null ? 6.0f : encapsulationSpeed;
        }

        @Override
        public Brightness brightness() {
            return brightness == null ? new Brightness(BrightnessMode.BY_GAME_FOG, null, null) : brightness;
        }

        @Override
        public Color color() {
            return color == null ? new Color(ColorMode.BY_GAME_FOG, null) : color;
        }

        @Override
        public FogShape shape() {
            return shape == null ? FogShape.SPHERE : shape;
        }

        public void validate() {
            if (condition == null) {
                throw new FoggyPaleGardenConfigurationException("Binding condition is not defined");
            }
            condition.validate();

            if (startDistance() < 0.0f) {
                throw new FoggyPaleGardenConfigurationException("Binding start distance is negative");
            }
            if (startDistance() > endDistance()) {
                throw new FoggyPaleGardenConfigurationException("Binding start distance is greater than end distance");
            }

            if (opacity() <= 0.0f || opacity() > 100.0f) {
                throw new FoggyPaleGardenConfigurationException("Binding opacity is out of range (0, 100]");
            }

            if (encapsulationSpeed() <= 0.0f) {
                throw new FoggyPaleGardenConfigurationException("Binding encapsulation speed is non-positive");
            }

            brightness().validate();

            color().validate();
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (code == null || code.isBlank()) {
            throw new FoggyPaleGardenConfigurationException("Preset code is not defined");
        }
        if (bindings == null || bindings.isEmpty()) {
            throw new FoggyPaleGardenConfigurationException("Preset bindings are not defined");
        }
        for (val binding : bindings) {
            binding.validate();
        }
    }
}
