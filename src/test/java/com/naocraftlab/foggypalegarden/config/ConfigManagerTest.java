package com.naocraftlab.foggypalegarden.config;

import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding.Brightness.BrightnessMode;
import com.naocraftlab.foggypalegarden.config.FogPresetV2.Binding.Color.ColorMode;
import lombok.val;
import net.minecraft.world.Difficulty;
import org.assertj.core.api.WithAssertions;
import org.json.JSONException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.naocraftlab.foggypalegarden.TestUtils.fileCount;
import static com.naocraftlab.foggypalegarden.util.FpgCollections.treeSetOf;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.writeString;

class ConfigManagerTest implements WithAssertions {

    @Nested
    class ReloadConfigsWithoutMigrationsTest {

        @Test
        void mustSuccessfullyCreateConfigAndPresetsInTheirAbsence() throws IOException, JSONException {
            val modId = "foggy-pale-garden";
            val configDirectory = createTempDirectory("initOnEmptyConfigTest").resolve("config");
            val expectedPresetDirectory = configDirectory.resolve("foggypalegarden");

            ConfigManager.init(configDirectory, modId);
            ConfigManager.reloadConfigs();

            assertThat(ConfigManager.currentConfig()).usingRecursiveComparison()
                    .isEqualTo(ModConfigV2.builder().preset("FPG_STEPHEN_KING").build());
            JSONAssert.assertEquals(
                    """
                    {
                      "preset": "FPG_STEPHEN_KING",
                      "version": 2
                    }
                    """,
                    readString(configDirectory.resolve(modId + ".json")),
                    true
            );

            val currentPresets = ConfigManager.allPresets();
            assertThat(currentPresets.get("FPG_AMBIANCE").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_AMBIANCE")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(95.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_I_AM_NOT_AFRAID_BUT").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_I_AM_NOT_AFRAID_BUT")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_STEPHEN_KING").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_STEPHEN_KING")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_DIFFICULTY_BASED").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_DIFFICULTY_BASED")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.PEACEFUL, Difficulty.EASY)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(95.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build(),
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.NORMAL)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build(),
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.HARD)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(fileCount(expectedPresetDirectory)).isEqualTo(4);
            JSONAssert.assertEquals(
                    """
                    {
                      "code": "FPG_AMBIANCE",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 2.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 15.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 95.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """,
                    readString(expectedPresetDirectory.resolve("FPG_AMBIANCE.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                    {
                      "code": "FPG_I_AM_NOT_AFRAID_BUT",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 2.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 15.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 100.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """,
                    readString(expectedPresetDirectory.resolve("FPG_I_AM_NOT_AFRAID_BUT.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                    {
                      "code": "FPG_STEPHEN_KING",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 0.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 10.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 100.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """,
                    readString(expectedPresetDirectory.resolve("FPG_STEPHEN_KING.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                    {
                      "code": "FPG_DIFFICULTY_BASED",
                      "bindings": [
                        {
                          "condition": {
                            "and": [
                              {
                                "difficultyIn": [
                                  "PEACEFUL",
                                  "EASY"
                                ]
                              },
                              {
                                "biomeIdIn": [
                                  "minecraft:pale_garden"
                                ]
                              }
                            ]
                          },
                          "startDistance": 2.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 15.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 95.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        },
                        {
                          "condition": {
                            "and": [
                              {
                                "difficultyIn": [
                                  "NORMAL"
                                ]
                              },
                              {
                                "biomeIdIn": [
                                  "minecraft:pale_garden"
                                ]
                              }
                            ]
                          },
                          "startDistance": 2.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 15.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 100.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        },
                        {
                          "condition": {
                            "and": [
                              {
                                "difficultyIn": [
                                  "HARD"
                                ]
                              },
                              {
                                "biomeIdIn": [
                                  "minecraft:pale_garden"
                                ]
                              }
                            ]
                          },
                          "startDistance": 0.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 10.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 100.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """,
                    readString(expectedPresetDirectory.resolve("FPG_DIFFICULTY_BASED.json")),
                    true
            );
        }

        @Test
        void mustCreatePresetsIfThereIsNoDirectory() throws IOException, JSONException {
            val modId = "foggy-pale-garden";
            val configDirectory = createTempDirectory("initOnEmptyConfigTest").resolve("config");
            createDirectories(configDirectory);
            val expectedPresetDirectory = configDirectory.resolve("foggypalegarden");
            writeString(
                    configDirectory.resolve(modId + ".json"),
                    """
                    {
                      "preset": "FPG_DIFFICULTY_BASED",
                      "version": 2
                    }
                    """
            );

            ConfigManager.init(configDirectory, modId);
            ConfigManager.reloadConfigs();

            assertThat(ConfigManager.currentConfig()).usingRecursiveComparison()
                    .isEqualTo(ModConfigV2.builder().preset("FPG_DIFFICULTY_BASED").build());
            JSONAssert.assertEquals(
                    """
                    {
                      "preset": "FPG_DIFFICULTY_BASED",
                      "version": 2
                    }
                    """,
                    readString(configDirectory.resolve(modId + ".json")),
                    true
            );

            val currentPresets = ConfigManager.allPresets();
            assertThat(currentPresets.get("FPG_AMBIANCE").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_AMBIANCE")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(95.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_I_AM_NOT_AFRAID_BUT").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_I_AM_NOT_AFRAID_BUT")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_STEPHEN_KING").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_STEPHEN_KING")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_DIFFICULTY_BASED").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_DIFFICULTY_BASED")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.PEACEFUL, Difficulty.EASY)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(95.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build(),
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.NORMAL)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build(),
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.HARD)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(fileCount(expectedPresetDirectory)).isEqualTo(4);
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_AMBIANCE",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 95.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_AMBIANCE.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_I_AM_NOT_AFRAID_BUT",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_I_AM_NOT_AFRAID_BUT.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_STEPHEN_KING",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 0.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 10.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_STEPHEN_KING.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_DIFFICULTY_BASED",
                              "bindings": [
                                {
                                  "condition": {
                                    "and": [
                                      {
                                        "difficultyIn": [
                                          "PEACEFUL",
                                          "EASY"
                                        ]
                                      },
                                      {
                                        "biomeIdIn": [
                                          "minecraft:pale_garden"
                                        ]
                                      }
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 95.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                },
                                {
                                  "condition": {
                                    "and": [
                                      {
                                        "difficultyIn": [
                                          "NORMAL"
                                        ]
                                      },
                                      {
                                        "biomeIdIn": [
                                          "minecraft:pale_garden"
                                        ]
                                      }
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                },
                                {
                                  "condition": {
                                    "and": [
                                      {
                                        "difficultyIn": [
                                          "HARD"
                                        ]
                                      },
                                      {
                                        "biomeIdIn": [
                                          "minecraft:pale_garden"
                                        ]
                                      }
                                    ]
                                  },
                                  "startDistance": 0.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 10.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_DIFFICULTY_BASED.json")),
                    true
            );
        }

        @Test
        void mustCreateConfigIfThereIsNotFound() throws IOException, JSONException {
            val modId = "foggy-pale-garden";
            val configDirectory = createTempDirectory("initOnEmptyConfigTest").resolve("config");
            val expectedPresetDirectory = configDirectory.resolve("foggypalegarden");
            createDirectories(expectedPresetDirectory);
            writeString(
                    expectedPresetDirectory.resolve("FPG_STEPHEN_KING.json"),
                    """
                    {
                      "code": "FPG_STEPHEN_KING",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 0.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 10.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 100.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """
            );

            ConfigManager.init(configDirectory, modId);
            ConfigManager.reloadConfigs();

            assertThat(ConfigManager.currentConfig()).usingRecursiveComparison()
                    .isEqualTo(ModConfigV2.builder().preset("FPG_STEPHEN_KING").build());
            JSONAssert.assertEquals(
                    """
                    {
                      "preset": "FPG_STEPHEN_KING",
                      "version": 2
                    }
                    """,
                    readString(configDirectory.resolve(modId + ".json")),
                    true
            );

            val currentPresets = ConfigManager.allPresets();
            assertThat(currentPresets).hasSize(1);
            assertThat(currentPresets.get("FPG_STEPHEN_KING").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_STEPHEN_KING")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(fileCount(expectedPresetDirectory)).isEqualTo(1);
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_STEPHEN_KING",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 0.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 10.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_STEPHEN_KING.json")),
                    true
            );
        }

        @Test
        void mustLoadConfigsAndPresetsAsIsIfTheyExist() throws IOException, JSONException {
            val modId = "foggy-pale-garden";
            val configDirectory = createTempDirectory("initOnEmptyConfigTest").resolve("config");
            val expectedPresetDirectory = configDirectory.resolve("foggypalegarden");
            createDirectories(expectedPresetDirectory);
            writeString(
                    configDirectory.resolve(modId + ".json"),
                    """
                    {
                      "preset": "FPG_AMBIANCE",
                      "version": 2
                    }
                    """
            );
            writeString(
                    expectedPresetDirectory.resolve("FPG_AMBIANCE.json"),
                    """
                    {
                      "code": "FPG_AMBIANCE",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 2.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 15.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 95.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """
            );
            writeString(
                    expectedPresetDirectory.resolve("FPG_STEPHEN_KING.json"),
                    """
                    {
                      "code": "FPG_STEPHEN_KING",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 0.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 10.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 100.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """
            );

            ConfigManager.init(configDirectory, modId);
            ConfigManager.reloadConfigs();

            assertThat(ConfigManager.currentConfig()).usingRecursiveComparison()
                    .isEqualTo(ModConfigV2.builder().preset("FPG_AMBIANCE").build());
            JSONAssert.assertEquals(
                    """
                    {
                      "preset": "FPG_AMBIANCE",
                      "version": 2
                    }
                    """,
                    readString(configDirectory.resolve(modId + ".json")),
                    true
            );

            val currentPresets = ConfigManager.allPresets();
            assertThat(currentPresets).hasSize(2);
            assertThat(currentPresets.get("FPG_AMBIANCE").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_AMBIANCE")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(95.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_STEPHEN_KING").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_STEPHEN_KING")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(fileCount(expectedPresetDirectory)).isEqualTo(2);
            JSONAssert.assertEquals(
                    """
                    {
                      "code": "FPG_AMBIANCE",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 2.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 15.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 95.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """,
                    readString(expectedPresetDirectory.resolve("FPG_AMBIANCE.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                    {
                      "code": "FPG_STEPHEN_KING",
                      "bindings": [
                        {
                          "condition": {
                            "biomeIdIn": [
                              "minecraft:pale_garden"
                            ]
                          },
                          "startDistance": 0.0,
                          "skyLightStartLevel": 4,
                          "endDistance": 10.0,
                          "surfaceHeightEnd": 15.0,
                          "opacity": 100.0,
                          "encapsulationSpeed": 6.0,
                          "brightness": {
                            "mode": "BY_GAME_FOG"
                          },
                          "color": {
                            "mode": "BY_GAME_FOG"
                          }
                        }
                      ],
                      "version": 2
                    }
                    """,
                    readString(expectedPresetDirectory.resolve("FPG_STEPHEN_KING.json")),
                    true
            );
        }

        // TODO exceptions
    }

    @Nested
    class ReloadConfigsWithMigrationsTest {

        @Test
        void shouldSuccessfullyMigrateConfigVersion1To2() throws IOException, JSONException {
            val modId = "foggy-pale-garden";
            val configDirectory = createTempDirectory("initOnEmptyConfigTest").resolve("config");
            createDirectories(configDirectory);
            val expectedPresetDirectory = configDirectory.resolve("foggypalegarden");
            writeString(
                    configDirectory.resolve(modId + ".json"),
                    """
                            {
                              "biomes": [
                                "minecraft:pale_garden"
                              ],
                              "fogPreset": "STEPHEN_KING",
                              "customFog": {
                                "startDistance": 0.0,
                                "skyLightStartLevel": 4,
                                "endDistance": 10.0,
                                "surfaceHeightEnd": 15.0,
                                "opacity": 100.0,
                                "encapsulationSpeed": 6.0
                              },
                              "version": 1
                            }
                            """
            );

            ConfigManager.init(configDirectory, modId);
            ConfigManager.reloadConfigs();


            assertThat(ConfigManager.currentConfig()).usingRecursiveComparison()
                    .isEqualTo(ModConfigV2.builder().preset("FPG_STEPHEN_KING").build());
            JSONAssert.assertEquals(
                    """
                            {
                              "preset": "FPG_STEPHEN_KING",
                              "version": 2
                            }
                            """,
                    readString(configDirectory.resolve(modId + ".json")),
                    true
            );

            val currentPresets = ConfigManager.allPresets();
            assertThat(currentPresets).hasSize(5);
            assertThat(currentPresets.get("CUSTOM").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("CUSTOM")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder()
                                                            .biomeIdIn(Set.of("minecraft:pale_garden"))
                                                            .build()
                                            )
                                            .startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(
                                                    FogPresetV2.Binding.Brightness.builder()
                                                            .mode(BrightnessMode.BY_GAME_FOG)
                                                            .build()
                                            ).color(
                                                    FogPresetV2.Binding.Color.builder()
                                                            .mode(ColorMode.BY_GAME_FOG)
                                                            .build()
                                            ).build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_I_AM_NOT_AFRAID_BUT").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_I_AM_NOT_AFRAID_BUT")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_AMBIANCE").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_AMBIANCE")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(95.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_STEPHEN_KING").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_STEPHEN_KING")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(FogPresetV2.Binding.Condition.builder().biomeIdIn(Set.of("minecraft:pale_garden")).build())
                                            .startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );
            assertThat(currentPresets.get("FPG_DIFFICULTY_BASED").second()).usingRecursiveComparison().isEqualTo(
                    FogPresetV2.builder()
                            .code("FPG_DIFFICULTY_BASED")
                            .bindings(List.of(
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.PEACEFUL, Difficulty.EASY)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(95.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build(),
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.NORMAL)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(2.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(15.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build(),
                                    FogPresetV2.Binding.builder()
                                            .condition(
                                                    FogPresetV2.Binding.Condition.builder().and(List.of(
                                                            FogPresetV2.Binding.Condition.builder().difficultyIn(treeSetOf(Difficulty.HARD)).build(),
                                                            FogPresetV2.Binding.Condition.builder().biomeIdIn(treeSetOf("minecraft:pale_garden")).build()
                                                    )).build()
                                            ).startDistance(0.0f)
                                            .skyLightStartLevel(4)
                                            .endDistance(10.0f)
                                            .surfaceHeightEnd(15.0f)
                                            .opacity(100.0f)
                                            .encapsulationSpeed(6.0f)
                                            .brightness(FogPresetV2.Binding.Brightness.builder().mode(BrightnessMode.BY_GAME_FOG).build())
                                            .color(FogPresetV2.Binding.Color.builder().mode(ColorMode.BY_GAME_FOG).build())
                                            .build()
                            )).build()
            );

            assertThat(fileCount(expectedPresetDirectory)).isEqualTo(5);
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_AMBIANCE",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 95.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_AMBIANCE.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "CUSTOM",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 0.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 10.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("CUSTOM.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_I_AM_NOT_AFRAID_BUT",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_I_AM_NOT_AFRAID_BUT.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_STEPHEN_KING",
                              "bindings": [
                                {
                                  "condition": {
                                    "biomeIdIn": [
                                      "minecraft:pale_garden"
                                    ]
                                  },
                                  "startDistance": 0.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 10.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_STEPHEN_KING.json")),
                    true
            );
            JSONAssert.assertEquals(
                    """
                            {
                              "code": "FPG_DIFFICULTY_BASED",
                              "bindings": [
                                {
                                  "condition": {
                                    "and": [
                                      {
                                        "difficultyIn": [
                                          "PEACEFUL",
                                          "EASY"
                                        ]
                                      },
                                      {
                                        "biomeIdIn": [
                                          "minecraft:pale_garden"
                                        ]
                                      }
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 95.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                },
                                {
                                  "condition": {
                                    "and": [
                                      {
                                        "difficultyIn": [
                                          "NORMAL"
                                        ]
                                      },
                                      {
                                        "biomeIdIn": [
                                          "minecraft:pale_garden"
                                        ]
                                      }
                                    ]
                                  },
                                  "startDistance": 2.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 15.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                },
                                {
                                  "condition": {
                                    "and": [
                                      {
                                        "difficultyIn": [
                                          "HARD"
                                        ]
                                      },
                                      {
                                        "biomeIdIn": [
                                          "minecraft:pale_garden"
                                        ]
                                      }
                                    ]
                                  },
                                  "startDistance": 0.0,
                                  "skyLightStartLevel": 4,
                                  "endDistance": 10.0,
                                  "surfaceHeightEnd": 15.0,
                                  "opacity": 100.0,
                                  "encapsulationSpeed": 6.0,
                                  "brightness": {
                                    "mode": "BY_GAME_FOG"
                                  },
                                  "color": {
                                    "mode": "BY_GAME_FOG"
                                  }
                                }
                              ],
                              "version": 2
                            }
                            """,
                    readString(expectedPresetDirectory.resolve("FPG_DIFFICULTY_BASED.json")),
                    true
            );
        }

        // TODO partly migrations

        // TODO exceptions
    }

    // TODO currentConfig

    // TODO currentPresets

}
