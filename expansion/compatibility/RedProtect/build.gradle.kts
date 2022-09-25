repositories {
    maven {
        name = "redprotect-repo"
        url = uri("https://raw.githubusercontent.com/FabioZumbi12/RedProtect/mvn-repo/")
    }
}

dependencies {
    compileOnly("br.net.fabiozumbi12.RedProtect:RedProtect-Core:7.7.3") {
        exclude("*", "*")
    }

    compileOnly("br.net.fabiozumbi12.RedProtect:RedProtect-Spigot:7.7.3") {
        exclude("*", "*")
    }
}
