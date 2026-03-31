plugins {
    kotlin("jvm") version "2.3.10"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1" // optional but useful

    // id("io.papermc.paperweight.userdev") version "1.7.1"
    // ↑ Uncomment if Surveyor ever needs NMS access
}

group = property("group")!!
version = property("version")!!

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    // Adventure API (recommended for TileState name handling)
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    // paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    // ↑ Uncomment if you need NMS access later
}

val targetJavaVersion = 21

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion("1.21.1")
    }
}
