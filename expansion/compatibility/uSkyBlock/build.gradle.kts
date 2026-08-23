java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven("https://nexus.sirblobman.xyz/uskyblock-mirror/")
}

dependencies {
    compileOnly("com.github.rlf:uSkyBlock-API:3.6.1")
}
