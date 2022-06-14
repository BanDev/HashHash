import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.russellbanks"
version = "1.5.1"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(compose.desktop.currentOs)

    // Accompanist (Multiplatform fork) - https://github.com/Syer10/accompanist
    implementation(libs.accompanist.flowlayouts)

    // Aurora - https://github.com/kirill-grouchnikov/aurora
    implementation(libs.aurora.component)
    implementation(libs.aurora.theming)
    implementation(libs.aurora.window)

    // Kotlin Coroutines - https://github.com/Kotlin/kotlinx.coroutines
    implementation(libs.coroutines.core)

    // Crypto - https://github.com/appmattus/crypto
    implementation(libs.crypto.cryptohash)

    // Decompose - https://github.com/arkivanov/Decompose
    implementation(libs.decompose.core)
    implementation(libs.decompose.jetbrains)

    // Flow Extensions - https://github.com/hoc081098/FlowExt
    implementation(libs.flowext)

    // Klogging - https://github.com/klogging/klogging
    implementation(libs.klogging.jvm)
    implementation(libs.klogging.slf4j)

    // Detekt Formatting Plugin - https://github.com/detekt/detekt
    detektPlugins(libs.detekt.formatting)

    // KotlinX DateTime - https://github.com/Kotlin/kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // KotlinX Serialization - https://github.com/Kotlin/kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)

    // Ktor - https://github.com/ktorio/ktor
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentnegotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)

    // LWJGL - https://github.com/LWJGL/lwjgl3
    implementation(libs.lwjgl.core)
    implementation(libs.lwjgl.tinyfd)
    runtimeOnly(variantOf(libs.lwjgl.core) { classifier(lwjglNatives()) })
    runtimeOnly(variantOf(libs.lwjgl.tinyfd) { classifier(lwjglNatives()) })

    // Window Styler - https://github.com/MayakaApps/ComposeWindowStyler
    implementation(libs.windowstyler)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            modules("java.instrument", "java.management", "java.prefs", "jdk.unsupported")
            javaHome = System.getenv("JDK_18")
            packageName = project.name
            packageVersion = project.version.toString()
            description = "A Multiplatform GUI for Hashing."
            vendor = "Russell Banks"
            licenseFile.set(project.file("src/main/resources/gpl-3.0.rst"))
            linux {
                iconFile.set(project.file("src/main/resources/logo.png"))
                menuGroup = project.name
            }
            macOS {
                bundleID = "${project.group}.${project.name.toLowerCase()}"
            }
            windows {
                iconFile.set(project.file("src/main/resources/logo.ico"))
                menuGroup = project.name
                dirChooser = true
                upgradeUuid = "1A4C2D6B-AC84-47D4-A6EE-407A4AA8DED8"
            }
        }
    }
}

buildConfig {
    buildConfigField("String", "appName", "\"${project.name}\"")
    buildConfigField("String", "appVersion", provider { "\"${project.version}\"" })
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
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) } ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "natives-windows-x86"
        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}
