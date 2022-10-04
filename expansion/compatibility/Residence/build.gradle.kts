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
    compileOnly("net.zrips:residence:5.0.2.0")
}
