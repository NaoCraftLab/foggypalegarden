package com.naocraftlab

import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplyExtPropertiesPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project != project.rootProject) {
            return
        }

        def envMcVersion = System.getenv("mcVersion") ?: "local"
        def mergedProps = new Properties()
        def rootPropsFile = project.file("gradle.properties")
        if (rootPropsFile.exists()) {
            rootPropsFile.withInputStream { mergedProps.load(it) }
        }

        def mcVersionPropsFile = project.file("mcVersions/${envMcVersion}.properties")
        if (mcVersionPropsFile.exists()) {
            def mcProps = new Properties()
            mcVersionPropsFile.withInputStream { mcProps.load(it) }

            mcProps.each { k, v ->
                mergedProps.setProperty(k.toString(), v.toString())
            }
        }

        mergedProps.each { k, v -> project.ext.set(k.toString(), v) }

        project.subprojects { subprj ->
            mergedProps.each { k, v -> subprj.ext.set(k.toString(), v) }
        }
    }
}
