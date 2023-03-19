repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val spigotVersion = property("spigot.version") as String
    compileOnly("com.destroystokyo.paper:paper-api:$spigotVersion")
}
