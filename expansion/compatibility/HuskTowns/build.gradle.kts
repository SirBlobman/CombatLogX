java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven("https://repo.william278.net/releases/")
}

dependencies {
    compileOnly("net.william278:husktowns:2.6.1")
}
