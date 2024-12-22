package com.naocraftlab

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class LoadPropertiesSettingsPlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {
        def mcVersion = System.getenv("mcVersion") ?: "1.21"

        def rootPropsFile = new File(settings.rootDir, "gradle.properties")
        def rootProps = new Properties()
        if (rootPropsFile.exists()) {
            rootProps.load(rootPropsFile.newDataInputStream())
        }

        def mcVersionPropsFile = new File(settings.rootDir, "mcVersions/${mcVersion}.properties")
        if (mcVersionPropsFile.exists()) {
            def mcProps = new Properties()
            mcVersionPropsFile.withInputStream { mcProps.load(it) }
            mcProps.each { key, value ->
                rootProps[key] = value
            }
        }

        rootProps.each { key, value ->
            settings.extensions.extraProperties.set(key.toString(), value.toString())
        }
    }
}
