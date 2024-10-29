plugins {
    id("fabric-loom")

    id("com.gradleup.shadow")

    id("com.modrinth.minotaur")
    id("com.matthewprenger.cursegradle")
}

base {
    archivesName.set("${project.property("modId")}-fabric-${project.property("minecraftReleaseVersion")}")
}

dependencies {
    implementation(project(":common"))
    shadow(project(":common"))

    minecraft("com.mojang:minecraft:${project.property("minecraftFirstSnapshotVersion")}")
    mappings("net.fabricmc:yarn:${project.property("fabricYarnMappingsVersion")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("fabricLoaderMinVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabricApiVersion")}")
}

sourceSets {
    val main by getting {
        resources {
            srcDir(project(":common").layout.projectDirectory.dir("src/main/resources"))
        }
    }
}

tasks {
    processResources {
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
                "modGitHubReleases" to project.property("modGitHubReleases"),
                "modModrinth" to project.property("modModrinth"),
                "modCurseForge" to project.property("modCurseForge"),
                "modDiscord" to project.property("modDiscord"),
                "modKoFi" to project.property("modKoFi"),
                "modAuthorYouTube" to project.property("modAuthorYouTube"),
                "fabricLoaderMinVersion" to project.property("fabricLoaderMinVersion"),
                "fabricApiVersion" to project.property("fabricApiVersion"),
                "minecraftJavaVersion" to project.property("minecraftJavaVersion"),
                "minecraftFirstSnapshotFullVersion" to project.property("minecraftFirstSnapshotFullVersion")
            )
        }
    }

    shadowJar {
        archiveClassifier.set("dev")
        configurations = listOf(project.configurations.shadow.get())
        isZip64 = true
    }

    sourcesJar {
        from(project(":common").sourceSets.main.get().allSource)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
    }

    build {
        dependsOn(remapJar)
    }

    artifacts {
        archives(remapJar)
        archives(sourcesJar)
    }
}

val getChangelogForVersion: (String) -> String by project.ext

val modrinthToken = project.findProperty("modrinthToken")?.toString() ?: System.getenv("MODRINTH_TOKEN")
if (modrinthToken != null) {
    modrinth {
        token.set(modrinthToken)
        projectId.set(project.property("modId").toString())
        versionNumber.set(project.version.toString())
        versionName.set("${project.property("minecraftReleaseVersion")}-${project.property("modVersion")}")
        versionType.set("release")
        uploadFile.set(File("build/libs/${project.base.archivesName.get()}-${project.version}.jar"))
        changelog.set(getChangelogForVersion("${project.property("minecraftReleaseVersion")}-${project.property("modVersion")}"))
        gameVersions.set(project.property("fabricModrinthGameVersions").toString().split(',').map { it.trim() })
        loaders.set(project.property("fabricSupportedLoaders").toString().split(',').map { it.trim().lowercase() }.toSet())
        additionalFiles.set(listOf(File("build/libs/${project.base.archivesName.get()}-${project.version}-sources.jar")))

        dependencies {
            required.project("fabric-api")
            optional.project("modmenu")
        }
    }
}

val curseForgeApiKey = project.findProperty("curseforgeApiKey")?.toString() ?: System.getenv("CURSEFORGE_API_KEY")
if (curseForgeApiKey != null) {
    curseforge {
        apiKey = curseForgeApiKey
        project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
            id = project.property("modCurseForgeId")
            releaseType = "release"
            changelogType = "markdown"
            changelog = getChangelogForVersion("${project.property("minecraftReleaseVersion")}-${project.property("modVersion")}")
            gameVersionStrings.addAll(project.property("fabricCurseForgeGameVersions").toString().split(',').map { it.trim() }.toSet())
            gameVersionStrings.addAll(project.property("fabricSupportedLoaders").toString().split(',').map { it.trim() })
            // TODO side
            // gameVersionStrings.add("Client")
            gameVersionStrings.add("Java ${project.property("minecraftJavaVersion")}")
            mainArtifact(
                File("build/libs/${project.base.archivesName.get()}-${project.version}.jar"),
                    closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
                    displayName = "${project.property("minecraftReleaseVersion")}-${project.property("modVersion")}"
                }
            )
            addArtifact(
                File("build/libs/${project.base.archivesName.get()}-${project.version}-sources.jar"),
                closureOf<com.matthewprenger.cursegradle.CurseArtifact> {}
            )
            relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
                requiredDependency("fabric-api")
                optionalDependency("modmenu")
            })
        })
    }
}
