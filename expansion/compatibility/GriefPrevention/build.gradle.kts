java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

repositories {
    maven("https://nexus.sirblobman.xyz/jitpack-mirror/")
}

dependencies {
    compileOnly("com.github.GriefPrevention:GriefPrevention:17.0.0")
}
