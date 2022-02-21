import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev620"
}

group = "org.bandev"
version = "1.0"

repositories {
    google()
    mavenCentral()
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
dependencies {
    implementation(compose.desktop.currentOs)
    implementation(Aurora.window)
    implementation(Aurora.component)
    implementation(Aurora.theming)
    implementation(Crypto.cryptohash)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "HashHash"
            packageVersion = "1.0.0"
        }
    }
}

object Aurora {
    private const val groupID = "org.pushing-pixels"
    private const val aurora_version = "1.0.1"

    const val window = "$groupID:aurora-window:$aurora_version"
    const val component = "$groupID:aurora-component:$aurora_version"
    const val theming = "$groupID:aurora-theming:$aurora_version"
}

object Crypto {
    private const val groupID = "com.appmattus.crypto"
    private const val crypto_version = "0.7.0"

    const val cryptohash = "$groupID:cryptohash:$crypto_version"
}