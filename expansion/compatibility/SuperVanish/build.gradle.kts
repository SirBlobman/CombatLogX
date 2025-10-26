repositories {
    maven("https://nexus.sirblobman.xyz/proxy-public")
}

dependencies {
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19") {
        exclude("*", "*")
    }
}
