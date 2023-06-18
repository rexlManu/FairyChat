import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.userdev)
    alias(libs.plugins.shadow)
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
}

repositories {
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(libs.versions.minecraft)

    compileOnly(libs.jedis)
    compileOnly(libs.commandframework)
    compileOnly(libs.configlib)
    compileOnly(libs.guice)
    compileOnly(libs.luckperms)

    // Plugin dependencies
    compileOnly(libs.miniplaceholders)

    implementation(libs.bstats)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
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

    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll(versions)
    loaders.addAll(listOf("paper", "purpur", "folia"))
    changelog.set(System.getenv("MODRINTH_CHANGELOG"))
    dependencies {
        required.project("miniplaceholders")
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
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions.set(versions)
                dependencies {
                    hangar("MiniPlaceholders", "MiniPlaceholders") {
                        required.set(true)
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