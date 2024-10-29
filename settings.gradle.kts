pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        val fabricLoomVersion: String by settings
        id("fabric-loom") version fabricLoomVersion

        val modrinthPluginVersion: String by settings
        id("com.modrinth.minotaur") version modrinthPluginVersion

        val curseForgePluginVersion: String by settings
        id("com.matthewprenger.cursegradle") version curseForgePluginVersion

        id("com.gradleup.shadow") version "8.3.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val modId: String by settings
rootProject.name = modId

include("common")

val fabricEnabled: String by settings
if (fabricEnabled.toBoolean()) {
    include("fabric")
}

val neoforgeEnabled: String by settings
if (neoforgeEnabled.toBoolean()) {
    include("neoforge")
}
