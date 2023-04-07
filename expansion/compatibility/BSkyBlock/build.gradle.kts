java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    // -SNAPSHOT required since 1.22.0 has a bad version error.
    compileOnly("world.bentobox:bentobox:1.23.0-SNAPSHOT")

    // -SNAPSHOT required since 1.15.2 has a bad version error.
    compileOnly("world.bentobox:bskyblock:1.15.2-SNAPSHOT")
}
