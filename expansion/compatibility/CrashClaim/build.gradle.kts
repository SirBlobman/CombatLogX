java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://nexus.sirblobman.xyz/jitpack-mirror/")
}

dependencies {
    compileOnly("com.github.WhipDevelopment:CrashClaim:c697d3e9ef")
}
