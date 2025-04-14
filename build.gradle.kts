import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.1.20"
    id("maven-publish")
}

val libVersion: String by extra

group = "cn.rtast.krcon"
version = libVersion

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    jvm { compilerOptions.jvmTarget = JvmTarget.JVM_1_8 }
    mingwX64()
    linuxX64()
    linuxArm64()
    macosX64()
    macosArm64()
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    watchosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    compilerOptions {
        freeCompilerArgs.apply {
            add("-Xexpect-actual-classes")
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        nativeMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0")
        }
    }
}

publishing {
    repositories {
        maven("https://maven.rtast.cn/releases") {
            credentials {
                username = "RTAkland"
                password = System.getenv("PUBLISH_TOKEN")
            }
        }
    }
}