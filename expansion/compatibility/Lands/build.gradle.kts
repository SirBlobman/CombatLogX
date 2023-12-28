repositories {
    maven("https://nexus.sirblobman.xyz/proxy-jitpack/")
}

dependencies {
    compileOnly(project(":expansion:newbie-helper"))
    compileOnly("com.github.Angeschossen:LandsAPI:6.42.14")
}
