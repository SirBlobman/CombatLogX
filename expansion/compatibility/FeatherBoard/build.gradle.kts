repositories {
    maven {
        name = "sirblobman-private"
        url = uri("https://nexus.sirblobman.xyz/repository/private/")

        credentials {
            var currentUsername = System.getenv("MAVEN_DEPLOY_USERNAME")
            if(currentUsername == null) {
                currentUsername = property("mavenUsernameSirBlobman") as String
            }

            var currentPassword = System.getenv("MAVEN_DEPLOY_PASSWORD")
            if (currentPassword == null) {
                currentPassword = property("mavenPasswordSirBlobman") as String
            }

            username = currentUsername
            password = currentPassword
        }
    }
}

dependencies {
    compileOnly("be.maximvdw.featherboard:FeatherBoard:5.0.4")
}
