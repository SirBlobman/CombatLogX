pipeline {
    agent {
        label 'multi-java'
    }

    options {
        githubProjectProperty(projectUrlStr: 'https://github.com/SirBlobman/CombatLogX')
    }

    environment {
        DISCORD_URL = credentials('PUBLIC_DISCORD_WEBHOOK')
        MAVEN_DEPLOY = credentials('MAVEN_DEPLOY')
        JDK8 = '/opt/java/jdk1.8.0_503'
        JDK11 = '/opt/java/jdk-11.0.32'
        JDK16 = '/opt/java/jdk-16.0.2'
        JDK17 = '/opt/java/jdk-17.0.12'
        JDK21 = '/opt/java/jdk-21.0.12'
        JDK25 = '/opt/java/openjdk'
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Gradle: Build') {
            steps {
                withGradle {
                    script {
                        sh('./gradlew --refresh-dependencies --no-daemon clean build')
                    }
                }
            }
        }

        stage('Gradle: Publish') {
            when {
                branch 'main'
            }

            steps {
                withGradle {
                    sh('./gradlew --no-daemon publish')
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
                def description = """
                    **Branch:** ${env.GIT_BRANCH}
                    **Build:** ${env.BUILD_NUMBER}
                    **Status:** ${currentBuild.currentResult}
                """

                discordSend(
                    webhookURL: DISCORD_URL,
                    title: 'CombatLogX',
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    description: description.stripIndent(),
                    enableArtifactsList: false,
                    showChangeset: true
                )
            }
        }
    }
}
