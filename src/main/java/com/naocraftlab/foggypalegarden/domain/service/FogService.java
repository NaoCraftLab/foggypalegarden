package com.naocraftlab.foggypalegarden.domain.service;

import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding.Color.ColorMode;
import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogCharacteristics;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.List;
import java.util.function.Predicate;

@UtilityClass
public class FogService {

    private static List<Pair<Predicate<Environment>, FogPresetV3.Binding>> presetBindings;

    private static FogPresetV3.Binding currentBinding = null;

    public static void onCurrentPresetChange(FogPresetV3 currentPreset) {
        presetBindings = currentPreset.getBindings().stream()
                .map(binding -> new Pair<>(binding.condition().toPredicate(), binding))
                .toList();
    }

    public static FogCharacteristics calculateFogCharacteristics(Environment environment) {
        val candidate = presetBindings.stream()
                .filter(pair -> pair.first().test(environment))
                .map(Pair::second)
                .findFirst();
        if (candidate.isEmpty() && currentBinding == null) {
            return FogCharacteristics.builder().fogDensity(0.0f).build();
        }
        currentBinding = candidate.orElse(currentBinding);

        val fogDensity = candidate.isPresent()
                ? Math.min(environment.fogDensity() + currentBinding.encapsulationSpeed() / 100f / 20f, 1.0f)
                : Math.max(environment.fogDensity() - currentBinding.encapsulationSpeed() / 100f / 20f, 0.0f);
        return FogCharacteristics.builder()
                .startDistance(currentBinding.startDistance())
                .endDistance(currentBinding.endDistance())
                .color(calculateColor(environment.gameFogColor(), currentBinding, fogDensity))
                .shape(currentBinding.shape())
                .fogDensity(fogDensity)
                .build();
    }

    private static Color calculateColor(Color gameFogColor, FogPresetV3.Binding binding, float fogDensity) {
        float red;
        float green;
        float blue;
        val brightnessMode = binding.brightness().mode();
        val colorMode = binding.color().mode();
        if (brightnessMode == BrightnessMode.FIXED && colorMode == ColorMode.FIXED) {
            val brightness = binding.brightness().fixedBrightness() / 100.0f;
            red = Math.min(hexToRed(binding.color().fixedHex()) * brightness, 1.0f);
            green = Math.min(hexToGreen(binding.color().fixedHex()) * brightness, 1.0f);
            blue = Math.min(hexToBlue(binding.color().fixedHex()) * brightness, 1.0f);
        } else if (brightnessMode == BrightnessMode.BY_GAME_FOG && colorMode == ColorMode.FIXED) {
            val brightness = calculateBrightness(gameFogColor);
            red = Math.min(hexToRed(binding.color().fixedHex()) * brightness, 1.0f);
            green = Math.min(hexToGreen(binding.color().fixedHex()) * brightness, 1.0f);
            blue = Math.min(hexToBlue(binding.color().fixedHex()) * brightness, 1.0f);
            if (binding.brightness().adjustment() != null) {
                float adjustment = binding.brightness().adjustment();

                if (adjustment >= 0) {
                    red = red + adjustment * (1.0f - red);
                } else {
                    red = red + adjustment * red;
                }
                red = Math.max(0.0f, Math.min(red, 1.0f));

                if (adjustment >= 0) {
                    green = green + adjustment * (1.0f - green);
                } else {
                    green = green + adjustment * green;
                }
                green = Math.max(0.0f, Math.min(green, 1.0f));

                if (adjustment >= 0) {
                    blue = blue + adjustment * (1.0f - blue);
                } else {
                    blue = blue + adjustment * blue;
                }
                blue = Math.max(0.0f, Math.min(blue, 1.0f));
            }
        } else if (brightnessMode == BrightnessMode.FIXED && colorMode == ColorMode.BY_GAME_FOG) {
            val targetBrightness = binding.brightness().fixedBrightness() / 100.0f;
            val currentBrightness = calculateBrightness(gameFogColor);
            float scale = targetBrightness / currentBrightness;
            red = Math.min(gameFogColor.red() * scale, 1.0f);
            green = Math.min(gameFogColor.green() * scale, 1.0f);
            blue = Math.min(gameFogColor.blue() * scale, 1.0f);
        } else {    // BrightnessMode.BY_GAME_FOG, ColorMode.BY_GAME_FOG
            red = gameFogColor.red();
            green = gameFogColor.green();
            blue = gameFogColor.blue();
            if (binding.brightness().adjustment() != null) {
                float adjustment = binding.brightness().adjustment();

                if (adjustment >= 0) {
                    red = red + adjustment * (1.0f - red);
                } else {
                    red = red + adjustment * red;
                }
                red = Math.max(0.0f, Math.min(red, 1.0f));

                if (adjustment >= 0) {
                    green = green + adjustment * (1.0f - green);
                } else {
                    green = green + adjustment * green;
                }
                green = Math.max(0.0f, Math.min(green, 1.0f));

                if (adjustment >= 0) {
                    blue = blue + adjustment * (1.0f - blue);
                } else {
                    blue = blue + adjustment * blue;
                }
                blue = Math.max(0.0f, Math.min(blue, 1.0f));
            }
        }
        val alpha = (binding.opacity() > 0f) ? fogDensity * (binding.opacity() / 100f - 0.001f) : 0f;
        return Color.builder()
                .red(red)
                .green(green)
                .blue(blue)
                .alpha(alpha)
                .build();
    }

    private static float calculateBrightness(Color color) {
        return (0.299f * color.red() + 0.587f * color.green() + 0.114f * color.blue()) * color.alpha();
    }

    private static float hexToRed(String hex) {
        return Integer.parseInt(hex.substring(0, 2), 16) / 100.0f;
    }

    private static float hexToGreen(String hex) {
        return Integer.parseInt(hex.substring(2, 4), 16) / 100.0f;
    }

    private static float hexToBlue(String hex) {
        return Integer.parseInt(hex.substring(4, 6), 16) / 100.0f;
    }
}
