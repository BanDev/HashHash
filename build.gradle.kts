import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
}

group = "com.russellbanks"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs)

    // Aurora - https://github.com/kirill-grouchnikov/aurora
    implementation(libs.aurora.window)
    implementation(libs.aurora.component)
    implementation(libs.aurora.theming)

    // Crypto - https://github.com/appmattus/crypto
    implementation(libs.crypto.cryptohash)

    // Decompose - https://github.com/arkivanov/Decompose
    implementation(libs.decompose.core)
    implementation(libs.decompose.desktop)

    // KotlinX DateTime - https://github.com/Kotlin/kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // LWJGL - https://github.com/LWJGL/lwjgl3
    implementation(platform(libs.lwjgl.bom))
    implementation(libs.lwjgl.core)
    implementation(libs.lwjgl.nfd)
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives())
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = lwjglNatives())
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe,TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            modules("java.instrument", "jdk.unsupported")
            javaHome = System.getenv("JDK_18")
            packageName = "HashHash"
            packageVersion = "1.0.0"
            description = "A Multiplatform GUI for Hashing."
            vendor = "Russell Banks"
            licenseFile.set(project.file("src/main/resources/gpl-3.0.rst"))
            args += listOf("-static-libgcc", "-static-libstdc++")
            linux {
                iconFile.set(project.file("src/main/resources/hash.png"))
                menuGroup = "HashHash"
            }
            windows {
                iconFile.set(project.file("src/main/resources/hash.ico"))
                menuGroup = "HashHash"
                dirChooser = true
                upgradeUuid = "1A4C2D6B-AC84-47D4-A6EE-407A4AA8DED8"
            }
        }
    }
}

fun lwjglNatives() = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else
                "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) }                ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) }                           ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "natives-windows-x86"
        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}