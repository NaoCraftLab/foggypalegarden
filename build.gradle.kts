plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
}

version = project.property("modVersion").toString()
group = project.property("group").toString()

base {
    archivesName.set("${project.property("modId")}-${project.property("minecraftReleaseVersion")}")
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraftFirstSnapshotVersion")}")
    mappings("net.fabricmc:yarn:${project.property("fabricYarnMappingsVersion")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("fabricLoaderMinVersion")}")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(
            "modId" to project.property("modId"),
            "modVersion" to project.property("modVersion"),
            "modName" to project.property("modName"),
            "modDescription" to project.property("modDescription"),
            "modLicense" to project.property("modLicense"),
            "modHomepage" to project.property("modHomepage"),
            "modSources" to project.property("modSources"),
            "modIssueTracker" to project.property("modIssueTracker"),
            "modDiscord" to project.property("modDiscord"),
            "fabricLoaderMinVersion" to project.property("fabricLoaderMinVersion"),
            "minecraftJavaVersion" to project.property("minecraftJavaVersion"),
            "minecraftFirstSnapshotFullVersion" to project.property("minecraftFirstSnapshotFullVersion")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(project.property("minecraftJavaVersion").toString().toInt())
}

java {
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

fun getChangelogForVersion(version: String): String {
    val changelogFile = File("CHANGELOG.md")
    if (!changelogFile.exists()) {
        return "Файл CHANGELOG.md не найден"
    }

    val content = changelogFile.readText()
    val lines = content.lines()

    var isVersionFound = false
    var versionContent = StringBuilder()
    for (line in lines) {
        if (!isVersionFound) {
            if (line.trim() == "## $version") {
                isVersionFound = true
                continue
            }
        } else {
            if (line.trim().startsWith("## ")) {
                break
            }
            versionContent.appendLine(line)
        }
    }

    return if (isVersionFound) {
        versionContent.toString().trim()
    } else {
        throw GradleException("Version $version information was not found in CHANGELOG.md")
    }
}

modrinth {
    token.set(project.findProperty("modrinthToken")?.toString() ?: System.getenv("MODRINTH_TOKEN"))
    projectId.set(project.property("modId").toString())
    versionNumber.set(project.version.toString())
    versionName.set("${project.property("minecraftReleaseVersion")}-${project.property("modVersion")}")
    versionType.set("release")
    uploadFile.set(File("build/libs/${project.base.archivesName.get()}-${project.version}.jar"))
    changelog.set(getChangelogForVersion("${project.property("minecraftReleaseVersion")}-${project.property("modVersion")}"))
    gameVersions.set(listOf(project.property("minecraftFirstSnapshotVersion").toString()))
    loaders.set(project.property("fabricSupportedLoaders").toString().split(',').map { it.trim().lowercase() })
    additionalFiles.set(listOf(File("build/libs/${project.base.archivesName.get()}-${project.version}-sources.jar")))
}