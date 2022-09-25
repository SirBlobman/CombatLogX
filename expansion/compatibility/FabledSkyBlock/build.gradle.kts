repositories {
    maven {
        name = "songoda-public"
        url = uri("https://repo.songoda.com/repository/public/")
    }
}

dependencies {
    compileOnly("com.songoda:fabledskyblock:2.2.1") {
        exclude("*", "*")
    }
}
