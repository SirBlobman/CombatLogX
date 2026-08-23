repositories {
    maven("https://nexus.sirblobman.xyz/jitpack-mirror/")
}

dependencies {
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19") {
        exclude("*", "*")
    }
}
