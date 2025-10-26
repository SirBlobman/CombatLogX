repositories {
    maven("https://nexus.sirblobman.xyz/proxy-public")
}

dependencies {
    compileOnly(project(":expansion:newbie-helper"))
    compileOnly("com.github.angeschossen:LandsAPI:7.15.4")
}
