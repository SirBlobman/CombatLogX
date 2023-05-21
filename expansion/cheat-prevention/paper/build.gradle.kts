repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val spigotVersion = property("version.spigot") as String
    compileOnly("com.destroystokyo.paper:paper-api:$spigotVersion")
    compileOnly(project(":expansion:cheat-prevention:abstract"))
}
