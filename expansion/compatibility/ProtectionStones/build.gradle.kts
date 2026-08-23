java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    compileOnly("dev.espi:ProtectionStones:2.10.6")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.14")
}
