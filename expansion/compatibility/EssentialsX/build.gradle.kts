repositories {
    maven("https://repo.essentialsx.net/releases/")
}

dependencies {
    compileOnly("net.essentialsx:EssentialsX:2.21.0") {
        exclude("io.papermc")
    }
}
