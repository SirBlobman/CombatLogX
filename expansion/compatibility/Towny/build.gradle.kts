java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://nexus.sirblobman.xyz/jitpack-mirror/")
}

dependencies {
    compileOnly("net.kyori:adventure-api:5.2.0")
    compileOnly("com.palmergames.bukkit.towny:towny:0.103.2.0")
    compileOnly("com.github.TownyAdvanced:FlagWar:0.8.1")
}
