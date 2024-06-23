java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly("world.bentobox:bentobox:2.4.0-SNAPSHOT") // BentoBox
    compileOnly("world.bentobox:bskyblock:1.18.0-SNAPSHOT") // BSkyBlock
}
