repositories {
    maven("https://nexus.sirblobman.xyz/jitpack-mirror/")
}

dependencies {
    compileOnly(project(":expansion:newbie-helper"))
    compileOnly("com.github.angeschossen:LandsAPI:7.15.4")
}
