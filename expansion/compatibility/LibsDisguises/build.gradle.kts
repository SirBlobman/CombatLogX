repositories {
    maven {
        name = "md5-public"
        url = uri("https://repo.md-5.net/repository/public/")
    }
}

dependencies {
    compileOnly("LibsDisguises:LibsDisguises:10.0.32") {
        exclude("*", "*")
    }
}
