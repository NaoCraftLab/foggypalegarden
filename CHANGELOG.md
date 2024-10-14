## 1.21.2-2.5.0

### Added

- Basic support for ModMenu with mod description translation into all supported languages

## 1.21.2-2.4.1

### Fixed

- Incorrect sky color causing the horizon to be visible through the fog

## 1.21.2-2.4.0

### Added

- The command `/fpg noFogGameMode <gameMode>` allows you to disable or enable the application of fog settings for the selected game mode

## 1.21.2-2.3.0

### Added

- Mod configuration management commands (even without operator permissions)
  - `/fpg preset` will display the name of the current preset
  - `/fpg preset FPG_DIFFICULTY_BASED` will switch the preset to FPG_DIFFICULTY_BASED (the command itself suggests a list of available presets)
  - `/fpg reloadConfig` re-reads the mod configuration and available fog preset files from disk
- The mod has been translated into multiple languages
  - 🇸🇦 Arabic
  - 🇧🇾 Belarusian
  - 🇨🇳 Chinese (Simplified)
  - 🇺🇸 English (US)
  - 🇫🇷 French
  - 🇬🇪 Georgian
  - 🇩🇪 German
  - 🇮🇹 Italian
  - 🇯🇵 Japanese
  - 🇰🇿 Kazakh
  - 🇰🇷 Korean
  - 🇧🇷 Portuguese (Brazil)
  - 🇷🇺 Russian
  - 🇪🇸 Spanish (Mexico)
  - 🇸🇪 Swedish
  - 🇺🇦 Ukrainian

## 1.21.2-2.2.0

### Added

- Brightness adjustment based on in-game fog brightness (you can increase or decrease the brightness calculated by the game)

## 1.21.2-2.1.0

### Added

- Condition for applying fog based on the dimension the player is in
- Condition for applying fog based on the temperature of the biome the player is in
- Fog shape control (available options: SPHERE and CYLINDER)
- Disable fog for selected game modes

## 1.21.2-2.0.0

### Added
- New fog preset format allowing you to add different fog based on selected conditions
- Automatic migration from configuration v1 to v2
- Ability to save any number of fog presets and select the active one through the mod’s config
- Presets can apply different fog settings based on biome, difficulty level, weather, and time
- Preset application conditions support logical operations AND, OR, and NOT
- When configuring fog, you can set the color based on in-game fog or a custom HEX value
- When configuring fog, you can set the brightness based on in-game fog or a custom value

## 1.21.2-1.2.1

### Fixed
- Game crash when the config folder is missing
- Fixed fog transparency caused by block culling, revealing the void behind it

## 1.21.2-1.2.0

### Added

- Mod configuration moved to the `config/foggy-pale-garden.json` file. Now you can customize everything to your liking!
- Fog presets. You can choose from predefined options (AMBIANCE, I_AM_NOT_AFRAID_BUT, or STEPHEN_KING).
- DIFFICULTY_BASED preset that changes the fog based on the world's difficulty.
- CUSTOM preset that allows you to set up the fog yourself.

### Changed

- A new beautiful icon!

## 1.21.2-1.1.0

### Added
- The fog does not fill caves beneath the Pale Garden
- The fog doesn’t hinder flying over the Pale Garden
- Quilt support

### Changed
- Improved the effect of entering and exiting the fog

### Fixed
- Removed dependency on Fabric API

## 1.21.2-1.0.0

### Added
- Envelops the Pale Garden in fog
