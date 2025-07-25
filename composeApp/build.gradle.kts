import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta03")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

            // Ktor Server
            implementation("io.ktor:ktor-server-core:3.2.2")
            implementation("io.ktor:ktor-server-netty:3.2.2")
            implementation("io.ktor:ktor-server-content-negotiation:3.2.2")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")
            implementation("io.ktor:ktor-server-call-logging:3.2.2")
            implementation("io.ktor:ktor-server-html-builder:3.2.2")
            implementation("io.ktor:ktor-server-resources:3.2.2")


            // Kotlinx Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

            // Ktor Server
            implementation("io.ktor:ktor-server-core:3.2.2")
            implementation("io.ktor:ktor-server-netty:3.2.2")
            implementation("io.ktor:ktor-server-content-negotiation:3.2.2")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")
            implementation("io.ktor:ktor-server-call-logging:3.2.2")
            implementation("io.ktor:ktor-server-html-builder:3.2.2")
            implementation("io.ktor:ktor-server-resources:3.2.2")



            // Kotlinx Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
        }
    }
}


compose.desktop {
    application {
        mainClass = ".org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}
