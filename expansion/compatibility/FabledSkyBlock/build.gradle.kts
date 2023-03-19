repositories {
    maven("https://repo.songoda.com/repository/public/")
}

dependencies {
    compileOnly("com.songoda:fabledskyblock:2.2.1") {
        exclude("*", "*")
    }
}
