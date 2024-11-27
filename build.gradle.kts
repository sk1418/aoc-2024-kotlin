plugins {
    kotlin("jvm") version "2.0.21"
}

repositories {
    mavenCentral()
}

java.setTargetCompatibility(22)

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_22
    }
}
tasks {
    sourceSets {
        main {
            kotlin.srcDirs("src")
        }
    }
    wrapper {
        gradleVersion = "8.11.1"
    }
}
