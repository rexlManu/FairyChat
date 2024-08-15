import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.shadow)
    // Plugins for publishing on platforms
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
    // Plugins to generate plugin metadata files
    alias(libs.plugins.paperyml)
    alias(libs.plugins.pluginyml)
}

repositories {
    mavenCentral()
    maven() {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.spigotmc")
            includeGroup("org.bukkit")
        }
    }
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.techscode.com/repository/maven-releases/")
}

dependencies {
    compileOnly(libs.spigot)

    library(libs.jedis)
    library(libs.commandframework)
    library(libs.configlib)
    library(libs.guice)
    library(libs.hikaridb)
    library(libs.stringsimilarity)
    library(libs.bundles.adventure)

    // Plugin dependencies
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.luckperms)
    compileOnly(libs.ultrapermissions)

    implementation(libs.bstats)
    implementation(projects.paper)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    build {
        dependsOn("shadowJar")
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
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

        dependencies {
            exclude("META-INF/NOTICE")
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
            exclude("META-INF/**.kotlin_module")
        }
    }
    runServer {
        minecraftVersion("1.20.4")
    }
}

tasks.getByName("modrinth").dependsOn(tasks.modrinthSyncBody)

val versions = listOf("1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1");

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

bukkit {
    author = "rexlManu"
    main = "de.rexlmanu.fairychat.plugin.FairyChatPlugin"
    website = "https://github.com/rexlManu/FairyChat"
    foliaSupported = true
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    softDepend = listOf("MiniPlaceholders", "LuckPerms", "PlaceholderAPI", "UltraPermissions")
    prefix = "FairyChat"
    generateLibrariesJson = true
}

paper {
    main = "de.rexlmanu.fairychat.plugin.FairyChatPlugin"
    loader = "de.rexlmanu.fairychat.plugin.paper.FairyChatLoader"
    apiVersion = "1.20"
    foliaSupported = true
    author = "rexlManu"
    website = "https://github.com/rexlManu/FairyChat"
    prefix = "FairyChat"
    serverDependencies {
        register("MiniPlaceholders") {
            required = false
        }
        register("LuckPerms") {
            required = false
        }
        register("PlaceholderAPI") {
            required = false
        }
        register("UltraPermissions") {
            required = false
        }
    }
    generateLibrariesJson = true
}