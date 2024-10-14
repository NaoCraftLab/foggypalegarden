plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("com.matthewprenger.cursegradle") version "1.4.0"
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
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    minecraft("com.mojang:minecraft:${project.property("minecraftFirstSnapshotVersion")}")
    mappings("net.fabricmc:yarn:${project.property("fabricYarnMappingsVersion")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("fabricLoaderMinVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabricApiVersion")}")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito:mockito-core:4.0.0")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
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

java {
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(project.property("minecraftJavaVersion").toString().toInt())
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}

tasks.test {
    useJUnitPlatform()
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
            })
        })
    }
}
