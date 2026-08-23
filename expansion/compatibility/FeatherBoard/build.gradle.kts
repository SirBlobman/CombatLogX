java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

fun getEnvOrProp(variableName: String, propertyName: String): String {
    val environmentProvider = providers.environmentVariable(variableName)
    val propertyProvider = providers.gradleProperty(propertyName)
    return environmentProvider.orElse(propertyProvider).orElse("").get()
}

repositories {
    maven("https://nexus.sirblobman.xyz/private/") {
        credentials {
            username = getEnvOrProp("MAVEN_DEPLOY_USR", "maven.username.sirblobman")
            password = getEnvOrProp("MAVEN_DEPLOY_PSW", "maven.password.sirblobman")
        }
    }
}

dependencies {
    compileOnly("com.mvdw-software:FeatherBoard:6.0.8")
}
