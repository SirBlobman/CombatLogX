repositories {
    maven("https://repo.codemc.io/repository/bentoboxworld/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly("world.bentobox:bentobox:3.3.5-SNAPSHOT") // BentoBox
    compileOnly("world.bentobox:bskyblock:1.19.1-SNAPSHOT") // BSkyBlock
}
