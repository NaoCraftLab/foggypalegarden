pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        val fabricLoomVersion: String by settings
        id("fabric-loom") version fabricLoomVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val modId: String by settings
rootProject.name = modId
