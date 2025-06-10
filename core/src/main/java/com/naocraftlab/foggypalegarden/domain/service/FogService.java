package com.naocraftlab.foggypalegarden.domain.service;

import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3;
import com.naocraftlab.foggypalegarden.config.preset.FogPresetV3.Binding;
import com.naocraftlab.foggypalegarden.domain.model.Color;
import com.naocraftlab.foggypalegarden.domain.model.Environment;
import com.naocraftlab.foggypalegarden.domain.model.FogCharacteristics;
import com.naocraftlab.foggypalegarden.domain.model.FogMode;
import com.naocraftlab.foggypalegarden.domain.model.FogShape;
import com.naocraftlab.foggypalegarden.util.Pair;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

@UtilityClass
public class FogService {

    private static List<Pair<Predicate<Environment>, Binding>> presetBindings = List.of();
    private static Binding latestFogBinding = null;
    private static Binding newFogBinding = latestFogBinding;

    private static boolean canApplyFog = false;

    private static float fogDensity = 0.0f;

    public static void onCurrentPresetChange(FogPresetV3 currentPreset) {
        presetBindings = currentPreset.getBindings().stream()
                .map(binding -> new Pair<>(binding.condition().toPredicate(), binding))
                .toList();
    }

    public static void changeFogBinding(Environment environment) {
        if (newFogBinding != null) {
            latestFogBinding = newFogBinding;
        }

        canApplyFog = environment.canApplyFog();
        if (canApplyFog) {
            newFogBinding = presetBindings.stream()
                    .filter(pair -> pair.first().test(environment))
                    .map(Pair::second)
                    .findFirst()
                    .orElse(null);
        }
    }


    public static Color calculateFogColor(Color gameFogColor) {
        if (!canApplyFog) {
            return gameFogColor;
        }

        final Binding targetBinding = targetBinding();
        if (targetBinding == null) {
            return gameFogColor;
        }

        float red;
        float green;
        float blue;
        val brightnessMode = targetBinding.brightness().mode();
        val colorMode = targetBinding.color().mode();
        if (brightnessMode == Binding.Brightness.BrightnessMode.FIXED && colorMode == Binding.Color.ColorMode.FIXED) {
            val brightness = targetBinding.brightness().fixedBrightness() / 100.0f;
            red = Math.min(hexToRed(targetBinding.color().fixedHex()) * brightness, 1.0f);
            green = Math.min(hexToGreen(targetBinding.color().fixedHex()) * brightness, 1.0f);
            blue = Math.min(hexToBlue(targetBinding.color().fixedHex()) * brightness, 1.0f);
        } else if (brightnessMode == Binding.Brightness.BrightnessMode.BY_GAME_FOG && colorMode == Binding.Color.ColorMode.FIXED) {
            val brightness = calculateBrightness(gameFogColor);
            red = Math.min(hexToRed(targetBinding.color().fixedHex()) * brightness, 1.0f);
            green = Math.min(hexToGreen(targetBinding.color().fixedHex()) * brightness, 1.0f);
            blue = Math.min(hexToBlue(targetBinding.color().fixedHex()) * brightness, 1.0f);
            if (targetBinding.brightness().adjustment() != null) {
                float adjustment = targetBinding.brightness().adjustment();

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
        } else if (brightnessMode == Binding.Brightness.BrightnessMode.FIXED && colorMode == Binding.Color.ColorMode.BY_GAME_FOG) {
            val targetBrightness = targetBinding.brightness().fixedBrightness() / 100.0f;
            val currentBrightness = calculateBrightness(gameFogColor);
            float scale = targetBrightness / currentBrightness;
            red = Math.min(gameFogColor.red() * scale, 1.0f);
            green = Math.min(gameFogColor.green() * scale, 1.0f);
            blue = Math.min(gameFogColor.blue() * scale, 1.0f);
        } else {    // BrightnessMode.BY_GAME_FOG, ColorMode.BY_GAME_FOG
            red = gameFogColor.red();
            green = gameFogColor.green();
            blue = gameFogColor.blue();
            if (targetBinding.brightness().adjustment() != null) {
                float adjustment = targetBinding.brightness().adjustment();

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
        return Color.builder()
                .red(clamp(gameFogColor.red(), red, fogDensity))
                .green(clamp(gameFogColor.green(), green, fogDensity))
                .blue(clamp(gameFogColor.blue(), blue, fogDensity))
                .alpha(clamp(1.0f, targetBinding.opacity() / 100.0f, fogDensity))
                .build();
    }

    @Nullable
    public static FogCharacteristics calculateFogCharacteristics(FogMode fogMode, float viewDistance) {
        if (!canApplyFog) {
            return null;
        }

        final Binding targetBinding = targetBinding();
        if (targetBinding == null) {
            return null;
        }
        fogDensity = newFogBinding != null
                ? Math.min(fogDensity + targetBinding.encapsulationSpeed() / 100f / 20f, 1.0f)
                : Math.max(fogDensity - targetBinding.encapsulationSpeed() / 100f / 20f, 0.0f);

        final float startDistance;
        final float endDistance;
        final FogShape shape;
        if (fogMode == FogMode.FOG_SKY) {
            startDistance = 0.0f;
            endDistance = viewDistance;
            shape = FogShape.CYLINDER;
        } else {
            val h = Mth.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
            val start = viewDistance - h;
            val end = viewDistance;

            startDistance = clamp(start, targetBinding.startDistance(), fogDensity);
            endDistance = clamp(end, targetBinding.endDistance(), fogDensity);
            shape = targetBinding.shape();
        }
        return FogCharacteristics.builder()
                .startDistance(startDistance)
                .endDistance(endDistance)
                .shape(shape)
                .build();
    }


    @Nullable
    private static Binding targetBinding() {
        if (newFogBinding != null) {
            return newFogBinding;
        } else if (latestFogBinding != null && fogDensity > 0.0f) {
            return latestFogBinding;
        } else {
            return null;
        }
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

    private static float calculateBrightness(Color color) {
        return (0.299f * color.red() + 0.587f * color.green() + 0.114f * color.blue()) * color.alpha();
    }

    private static float clamp(float source, float target, float modifier) {
        val clampedModifier = Mth.clamp(modifier, 0.0f, 1.0f);
        return source + (target - source) * clampedModifier;
    }
}
