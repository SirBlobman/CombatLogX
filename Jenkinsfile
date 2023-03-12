pipeline {
    agent any

    options {
        githubProjectProperty(projectUrlStr: "https://github.com/SirBlobman/CombatLogX")
    }

    environment {
        DISCORD_URL = credentials('PUBLIC_DISCORD_WEBHOOK')
        MAVEN_DEPLOY = credentials('MAVEN_DEPLOY')
    }

    triggers {
        githubPush()
    }

    tools {
        jdk "JDK 17"
    }

    stages {
        stage("Gradle: Build") {
            steps {
                withGradle {
                    script {
                        if (env.BRANCH_NAME == "main") {
                            sh("./gradlew clean build publish --refresh-dependencies --no-daemon")
                        } else {
                            sh("./gradlew clean build --refresh-dependencies --no-daemon")
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
                discordSend webhookURL: DISCORD_URL,
                        title: "${env.JOB_NAME}",
                        link: "${env.BUILD_URL}",
                        result: currentBuild.currentResult,
                        description: "**Build:** ${env.BUILD_NUMBER}\n**Status:** ${currentBuild.currentResult}",
                        enableArtifactsList: false,
                        showChangeset: true
            }
        }
    }
}
