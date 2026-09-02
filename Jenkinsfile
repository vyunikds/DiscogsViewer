pipeline {
    agent {
        docker {
            image 'discogsviewer-android-builder:17'
            reuseNode true
        }
    }

    environment {
        ANDROID_HOME = '/opt/android-sdk'
        ANDROID_SDK_ROOT = '/opt/android-sdk'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Inject secrets') {
            steps {
                // core/network/network.properties is gitignored, so the build
                // only works after the token is materialized from a credential
                withCredentials([string(credentialsId: 'discogs-token', variable: 'DISCOGS_TOKEN')]) {
                    sh 'mkdir -p core/network && printf "auth.token=%s\\n" "$DISCOGS_TOKEN" > core/network/network.properties'
                }
            }
        }

        stage('Unit tests') {
            steps {
                sh './gradlew test --stacktrace'
            }
        }

        stage('Static analysis') {
            steps {
                sh './gradlew detekt ktlintCheck --stacktrace'
            }
        }

        stage('Build release (main)') {
            when { branch 'main' }
            steps {
                sh './gradlew assembleRelease publishApkToLocalMaven --stacktrace'
            }
        }

        stage('Build debug (feature branches)') {
            when { branch '!main' }
            steps {
                sh './gradlew assembleDebug --stacktrace'
            }
        }

        stage('Archive artifacts') {
            steps {
                // empty patterns are skipped
                archiveArtifacts artifacts: 'app/build/outputs/apk/release/*.apk, app/build/outputs/apk/debug/*.apk, app/build/maven-local/**', allowEmptyArchive: true
            }
        }
    }

    post {
        failure {
            echo 'Build failed - check the logs above.'
        }
    }
}
