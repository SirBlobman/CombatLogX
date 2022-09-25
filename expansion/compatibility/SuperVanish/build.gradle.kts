repositories {
    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.12") {
        exclude("*", "*")
    }
}
