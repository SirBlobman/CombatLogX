repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    val spigotVersion = property("spigot.version") as String
    compileOnly("com.destroystokyo.paper:paper-api:$spigotVersion")
    compileOnly(project(":expansion:cheat-prevention:abstract"))
}
