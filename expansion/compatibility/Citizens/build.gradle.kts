repositories {
    maven {
        name = "everything"
        url = uri("https://repo.citizensnpcs.co/")
    }
}

dependencies {
    compileOnly("net.citizensnpcs:citizensapi:2.0.31-SNAPSHOT")
    compileOnly("org.mcmonkey:sentinel:2.7.3-SNAPSHOT")
}
