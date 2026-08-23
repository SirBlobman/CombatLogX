java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://nexus.sirblobman.xyz/jitpack-mirror/")
}

dependencies {
    compileOnly(project(":expansion:newbie-helper"))
    compileOnly("com.github.angeschossen:LandsAPI:7.15.4")
}
