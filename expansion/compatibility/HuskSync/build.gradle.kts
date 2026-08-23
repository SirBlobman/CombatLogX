java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven("https://repo.william278.net/releases/")
}

dependencies {
    compileOnly("net.william278.husksync:husksync-bukkit:3.8.5+1.21.6")
}
