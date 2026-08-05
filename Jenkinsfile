pipeline {
    agent any
    tools { jdk 'temurin-21'; nodejs 'node-24' }
    stages {
        stage('Backend') { steps { sh './gradlew test --no-daemon' } }
        stage('Frontend') {
            steps {
                sh 'npm --prefix frontend ci'
                sh 'npm --prefix frontend run test:ci'
                sh 'npm --prefix frontend run build'
            }
        }
        stage('Delivery descriptors') {
            steps {
                sh 'docker compose config --quiet'
                sh './scripts/audit-history.sh'
            }
        }
    }
}
