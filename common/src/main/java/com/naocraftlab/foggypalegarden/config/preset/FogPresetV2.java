package com.naocraftlab.foggypalegarden.config.preset;

import com.mojang.blaze3d.shaders.FogShape;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV2.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.exception.FoggyPaleGardenConfigurationException;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;
import net.minecraft.world.Difficulty;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, doNotUseGetters = true)
public final class FogPresetV2 extends FogPreset {

    /**
     * Preset code.
     */
    private final String code;

    /**
     * Bindings.
     */
    private final List<Binding> bindings;

    public FogPresetV2(String code, List<Binding> bindings) {
        super(2);
        this.code = code;
        this.bindings = bindings;
    }

    @Data
    @Builder
    public static class Binding {

        private final Condition condition;
        private final Float startDistance;
        private final Integer skyLightStartLevel;
        private final Float endDistance;
        private final Float surfaceHeightEnd;
        private final Float opacity;
        private final Float encapsulationSpeed;
        private final Brightness brightness;
        private final Color color;
        private final FogShape shape;

        @Data
        @Builder
        public static class Condition {

            private final Set<String> dimensionIn;
            private final Set<String> biomeIdIn;
            private final Temperature biomeTemperature;
            private final Set<Difficulty> difficultyIn;
            private final Set<Weather> weatherIn;
            private final TimePeriod timeIn;
            private final List<Condition> and;
            private final List<Condition> or;
            private final Condition not;

            public enum Weather {
                CLEAR,
                RAIN,
                THUNDER
            }

            @Data
            @Builder
            public static class TimePeriod {

                private final Long start;
                private final Long end;

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

            @Data
            @Builder
            public static class Temperature {

                private final Float min;
                private final Float max;

                public void validate() {
                    if (min == null && max == null) {
                        throw new FoggyPaleGardenConfigurationException("Temperature min and max cannot be both null");
                    }
                    if (min != null && max != null && min >= max) {
                        throw new FoggyPaleGardenConfigurationException("Temperature min must be less than max");
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
                        predicate = predicate.and(env -> dimensionIn.contains(env.getDimension()));
                    }
                    if (biomeIdIn != null && !biomeIdIn.isEmpty()) {
                        predicate = predicate.and(env -> biomeIdIn.contains(env.getBiome()));
                    }
                    if (biomeTemperature != null) {
                        val min = biomeTemperature.getMin();
                        val max = biomeTemperature.getMax();
                        predicate = predicate.and(env -> {
                            val temperature = env.getBiomeTemperature();
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
                        predicate = predicate.and(env -> difficultyIn.contains(env.getDifficulty()));
                    }
                    if (weatherIn != null && !weatherIn.isEmpty()) {
                        predicate = predicate.and(env -> weatherIn.contains(Weather.valueOf(env.getWeather().name())));
                    }
                    if (timeIn != null) {
                        val start = timeIn.getStart();
                        val end = timeIn.getEnd();
                        predicate = predicate.and(env -> {
                            val time = env.getTimeOfDay();
                            if (start <= end) {
                                return time >= start && time <= end;
                            } else {
                                return time >= start || time <= end;
                            }
                        });
                    }
                    return predicate;
                }
            }
        }

        @Data
        @Builder
        public static class Brightness {

            private final BrightnessMode mode;
            private final Float fixedBrightness;
            private final Float adjustment;

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

        @Data
        @Builder
        public static class Color {

            private final ColorMode mode;
            private final String fixedHex;

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

        public Float startDistance() {
            return startDistance == null ? 0.0f : startDistance;
        }

        public Float endDistance() {
            return endDistance == null ? 0.0f : endDistance;
        }

        public Float opacity() {
            return opacity == null ? 100.0f : opacity;
        }

        public Float encapsulationSpeed() {
            return encapsulationSpeed == null ? 6.0f : encapsulationSpeed;
        }

        public Brightness brightness() {
            return brightness == null ? new Brightness(BrightnessMode.BY_GAME_FOG, null, null) : brightness;
        }

        public Color color() {
            return color == null ? new Color(ColorMode.BY_GAME_FOG, null) : color;
        }

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

            if (skyLightStartLevel != null && (skyLightStartLevel < 0 || skyLightStartLevel > 15)) {
                throw new FoggyPaleGardenConfigurationException("Binding sky light start level is out of range [0, 15]");
            }

            if (surfaceHeightEnd != null && surfaceHeightEnd < 0.0f) {
                throw new FoggyPaleGardenConfigurationException("Binding surface height end is negative");
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
