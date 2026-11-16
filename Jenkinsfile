pipeline {
    agent any

    tools {
        // References Maven and JDK configured in Jenkins Global Tool Configuration
        maven 'Maven-3.9.6'
        jdk 'JDK-17'
    }

    triggers {
        // Poll SCM every 5 minutes (fallback if GitHub webhook is not reachable locally)
        pollSCM('H/5 * * * *')
    }

    options {
        // Keep last 10 builds to save disk space
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 15, unit: 'MINUTES')
        timestamps()
    }

    environment {
        APP_NAME = 'password-checker'
        JAR_NAME = 'password-checker-0.0.1-SNAPSHOT.jar'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from SCM...'
                checkout scm
            }
        }

        stage('Test') {
            steps {
                echo '=================================================='
                echo 'CI Enhancement: Running Automated JUnit 5 Unit Tests'
                echo '=================================================='
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    // Record and display JUnit test results in Jenkins UI
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                echo '=================================================='
                echo 'Building Executable JAR (skipping redundant tests)'
                echo '=================================================='
                script {
                    if (isUnix()) {
                        sh 'mvn -DskipTests clean package'
                    } else {
                        bat 'mvn -DskipTests clean package'
                    }
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                echo '=================================================='
                echo 'Archiving Built JAR Artifact'
                echo '=================================================='
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
            }
        }
    }

    post {
        success {
            echo "SUCCESS: Pipeline completed successfully! Application '${APP_NAME}' packaged and archived."
        }
        failure {
            echo "FAILURE: Build failed! Check the test reports and console output for errors."
        }
    }
}

