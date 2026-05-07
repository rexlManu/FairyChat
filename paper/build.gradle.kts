plugins {
    `java-library`
    alias(libs.plugins.lombok)
}
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.guice)
}

tasks {
    compileJava {
        options.release.set(25)
        options.encoding = Charsets.UTF_8.name()
    }
}
