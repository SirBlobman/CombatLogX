java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly("world.bentobox:bentobox:1.23.0-SNAPSHOT") // BentoBox
    compileOnly("world.bentobox:bskyblock:1.15.2-SNAPSHOT") // BSkyBlock
}
