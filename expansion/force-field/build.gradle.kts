java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven("https://nexus.sirblobman.xyz/public/")
}

dependencies {
    compileOnly("com.comphenix.protocol:ProtocolLib:5.4.0")
}
