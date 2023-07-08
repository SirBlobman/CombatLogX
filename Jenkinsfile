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
                        sh("./gradlew --refresh-dependencies --no-daemon clean build")
                        if (env.BRANCH_NAME == "main") {
                            sh("./gradlew publish")
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
