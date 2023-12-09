repositories {
    maven("https://nexus.sirblobman.xyz/proxy-jitpack/")
}

dependencies {
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.18-3") {
        exclude("*", "*")
    }
}
