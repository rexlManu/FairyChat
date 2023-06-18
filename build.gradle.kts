plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.userdev)
    alias(libs.plugins.shadow)
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