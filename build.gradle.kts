import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.userdev)
    alias(libs.plugins.shadow)
    // Plugins for publishing on platforms
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
    // Plugins to generate plugin metadata files
    alias(libs.plugins.paperyml)
    alias(libs.plugins.pluginyml)
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(libs.versions.minecraft)

    compileOnly(libs.jedis)
    compileOnly(libs.commandframework)
    compileOnly(libs.configlib)
    compileOnly(libs.guice)
    compileOnly(libs.luckperms)
    compileOnly(libs.hikaridb)

    // Plugin dependencies
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.placeholderapi)

    implementation(libs.bstats)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()

    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    assemble {
        dependsOn(reobfJar)
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
        minimize()
    }
}

tasks.getByName("modrinth").dependsOn(tasks.modrinthSyncBody)

val versions = listOf("1.19.2", "1.19.4", "1.20", "1.20.1");

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("fairychat")

    versionNumber.set(rootProject.version.toString())
    versionName.set("FairyChat ${rootProject.version}")
    versionType.set("release")

    syncBodyFrom.set(rootProject.file("README.md").readText())

    uploadFile.set(buildDir.resolve("libs").resolve("FairyChat-${rootProject.version}.jar"))
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
        namespace("rexlManu", "fairychat")
        channel.set("Release")
        changelog.set(System.getenv("HANGAR_CHANGELOG"))
        apiKey.set(System.getenv("HANGAR_TOKEN"))

        // register platforms
        platforms {
            register(Platforms.PAPER) {
                jar.set(buildDir.resolve("libs").resolve("FairyChat-${rootProject.version}.jar"))
                platformVersions.set(versions)
                dependencies {
                    hangar("MiniPlaceholders", "MiniPlaceholders") {
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
    softDepend = listOf("MiniPlaceholders", "LuckPerms", "PlaceholderAPI")
    prefix = "FairyChat"
}

paper {
    main = "de.rexlmanu.fairychat.plugin.FairyChatPlugin"
    loader = "de.rexlmanu.fairychat.plugin.FairyChatLoader"
    apiVersion = "1.19"
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
    }
}