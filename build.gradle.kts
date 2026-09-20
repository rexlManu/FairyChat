import io.github.themrmilchmann.gradle.publish.curseforge.ChangelogFormat
import io.github.themrmilchmann.gradle.publish.curseforge.CurseForgePublishingExtension
import io.github.themrmilchmann.gradle.publish.curseforge.ReleaseType
import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.GeneratePluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder

plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.shadow)
    // Plugins for publishing on platforms
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforge)
    // Plugins to generate plugin metadata files
    alias(libs.plugins.paperyml)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.miopowered.eu/snapshots/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly(libs.paper)

    library(libs.jedis)
    library(libs.commandframework)
    library(libs.configlib)
    library(libs.guice)
    library(libs.hikaridb)
    library(libs.stringsimilarity)

    // Plugin dependencies
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.luckperms)
    compileOnly(libs.ultrapermissions)

    implementation(libs.bstats)

    testImplementation(libs.paper)
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

configurations.testImplementation {
    extendsFrom(configurations.library.get())
}

lombok {
    version = libs.versions.lombok.get()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(25)
    }
    build {
        dependsOn("shadowJar")
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    jar {
        enabled = false
    }
    shadowJar {
        archiveClassifier.set("")
        relocate("org.bstats", "de.rexlmanu.fairychat.dependencies.bstats")
        from(file("LICENSE"))

        exclude("META-INF/NOTICE", "META-INF/maven/**", "META-INF/**.kotlin_module")
    }
    runServer {
        minecraftVersion(libs.versions.minecraft.get())
    }
}

val versions = listOf(libs.versions.minecraft.get())

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("fairychat")

    versionNumber.set(rootProject.version.toString())
    versionName.set("FairyChat ${rootProject.version}")
    versionType.set("release")

    syncBodyFrom.set(rootProject.file("README.md").readText())

    uploadFile.set(layout.buildDirectory.file("libs/FairyChat-${rootProject.version}.jar"))
    gameVersions.addAll(versions)
    loaders.addAll(listOf("paper", "purpur", "folia"))
    changelog.set(System.getenv("MODRINTH_CHANGELOG"))
    dependencies {
        optional.project("miniplaceholders")
        optional.project("luckperms")
    }
}

tasks.named("modrinth") {
    dependsOn(tasks.shadowJar)
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        id.set("fairychat")
        channel.set("Release")
        changelog.set(System.getenv("HANGAR_CHANGELOG"))
        apiKey.set(System.getenv("HANGAR_TOKEN"))

        // register platforms
        platforms {
            register(Platforms.PAPER) {
                jar.set(layout.buildDirectory.file("libs/FairyChat-${rootProject.version}.jar"))
                platformVersions.set(versions)
                dependencies {
                    hangar("MiniPlaceholders") {
                        required.set(false)
                    }
//                  Luckperms is not yet available on hangar
//                    hangar("luckPerms", "luckperms") {
//                        required.set(false)
//                    }
                }
            }
        }
    }
}

tasks.named("publishAllPublicationsToHangar") {
    dependsOn(tasks.shadowJar)
}

tasks.named("publishPluginPublicationToHangar") {
    dependsOn(tasks.shadowJar)
}

configure<CurseForgePublishingExtension> {
    apiToken.set(System.getenv("CURSEFORGE_TOKEN"))
    publications {
        register("plugin") {
            projectId.set("878649")
            versions.forEach { gameVersion("bukkit", it.replace(".", "-")) }

            artifacts {
                register("main") {
                    from(tasks.shadowJar)
                    displayName.set("FairyChat ${rootProject.version}")
                    releaseType.set(ReleaseType.RELEASE)
                    changelog {
                        format.set(ChangelogFormat.MARKDOWN)
                        content.set(System.getenv("CURSEFORGE_CHANGELOG") ?: "")
                    }
                    relations {
                        optionalDependency("miniplaceholders")
                        optionalDependency("luckperms")
                    }
                }
            }
        }
    }
}

tasks.named("publishToCurseForge") {
    dependsOn(tasks.shadowJar)
}

tasks.named("publishPluginPublicationToCurseForge") {
    dependsOn(tasks.shadowJar)
}

paper {
    main = "de.rexlmanu.fairychat.plugin.FairyChatPlugin"
    loader = "de.rexlmanu.fairychat.plugin.paper.FairyChatLoader"
    apiVersion = libs.versions.minecraft.get()
    foliaSupported = true
    author = "rexlManu"
    website = "https://github.com/rexlManu/FairyChat"
    prefix = "FairyChat"
    serverDependencies {
        register("MiniPlaceholders") {
            required = false
            load = RelativeLoadOrder.BEFORE
        }
        register("LuckPerms") {
            required = false
            load = RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            required = false
            load = RelativeLoadOrder.BEFORE
        }
        register("UltraPermissions") {
            required = false
            load = RelativeLoadOrder.BEFORE
        }
    }
    generateLibrariesJson = true
}

tasks.withType<GeneratePluginDescription>().configureEach {
    useDefaultCentralProxy()
}
