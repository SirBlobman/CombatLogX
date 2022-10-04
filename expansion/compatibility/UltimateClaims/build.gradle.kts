repositories {
    maven {
        name = "sirblobman-private"
        url = uri("https://nexus.sirblobman.xyz/repository/private/")

        credentials {
            username = rootProject.ext.get("mavenUsername") as String
            password = rootProject.ext.get("mavenPassword") as String
        }
    }
}

dependencies {
    compileOnly("com.songoda:UltimateClaims:1.7.0")
}
