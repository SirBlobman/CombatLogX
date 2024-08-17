pipeline {
    agent any

    options {
        githubProjectProperty(projectUrlStr: "https://github.com/SirBlobman/CombatLogX")
    }

    environment {
        DISCORD_URL = credentials('PUBLIC_DISCORD_WEBHOOK')
        MAVEN_DEPLOY = credentials('MAVEN_DEPLOY')
        JDK8 = '/home/container/jdk/zulu8.78.0.19-ca-jdk8.0.412-linux_x64'
        JDK17 = '/home/container/jdk/zulu17.50.19-ca-jdk17.0.11-linux_x64'
    }

    triggers {
        githubPush()
    }

    tools {
        jdk "JDK 21"
    }

    stages {
        stage("Gradle: Build") {
            steps {
                withGradle {
                    script {
                        sh("./gradlew --refresh-dependencies --no-daemon clean build")
                        if (env.BRANCH_NAME == "main") {
                            sh("./gradlew publish --no-daemon")
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'builder/build/distributions/CombatLogX-*.zip', fingerprint: true
        }

        always {
            script {
                discordSend webhookURL: DISCORD_URL, title: "CombatLogX", link: "${env.BUILD_URL}",
                    result: currentBuild.currentResult,
                    description: """\
                        **Branch:** ${env.GIT_BRANCH}
                        **Build:** ${env.BUILD_NUMBER}
                        **Status:** ${currentBuild.currentResult}""".stripIndent(),
                    enableArtifactsList: false, showChangeset: true
            }
        }
    }
}
