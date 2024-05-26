repositories {
    maven("https://nexus.sirblobman.xyz/proxy-jitpack/")
}

dependencies {
    compileOnly(project(":expansion:newbie-helper"))
    compileOnly("com.github.angeschossen:LandsAPI:7.1.12")
}
