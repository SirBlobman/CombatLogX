repositories {
    maven("https://repo.md-5.net/repository/public/")
}

dependencies {
    compileOnly("LibsDisguises:LibsDisguises:10.0.37") {
        exclude("*", "*")
    }
}
