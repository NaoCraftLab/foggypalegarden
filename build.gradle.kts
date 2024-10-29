plugins {
    id("java")
}

subprojects {
    apply(plugin = "java")

    group = project.property("group").toString()
    version = project.property("modVersion").toString()

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:25.0.0")
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")

        testCompileOnly("org.jetbrains:annotations:25.0.0")
        testCompileOnly("org.projectlombok:lombok:1.18.34")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        testImplementation("org.mockito:mockito-core:4.0.0")
        testImplementation("org.assertj:assertj-core:3.21.0")
        testImplementation("org.skyscreamer:jsonassert:1.5.0")
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
