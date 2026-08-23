java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

dependencies {
    compileOnly("fr.nocsy:mcpets:4.1.6")
}
