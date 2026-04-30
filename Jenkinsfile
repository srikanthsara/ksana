pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
        REPO_URL = "https://github.com/srikanthsara/ksana.git"
        BRANCH = "dev"
        IMAGE_NAME = "ksana-backend"
        CONTAINER_NAME = "ksana-backend"
        APP_PORT = "8081"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "📥 Cloning Ksana repository..."
                git branch: "${BRANCH}", url: "${REPO_URL}"
            }
        }

        stage('Build') {
            steps {
                echo "🔨 Building project..."
                dir('backend') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                echo "🧪 Running tests..."
                dir('backend') {
                    sh 'mvn test'
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo "🐳 Building Docker image..."
                dir('backend') {
                    sh 'docker build -t ${IMAGE_NAME} .'
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                echo "🚀 Deploying container..."

                sh '''
                docker rm -f ${CONTAINER_NAME} || true

                docker run -d \
                  -p ${APP_PORT}:8081 \
                  --name ${CONTAINER_NAME} \
                  -e SPRING_PROFILES_ACTIVE=dev \
                  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/ksana_db \
                  -e SPRING_DATASOURCE_USERNAME=ksana_user \
                  -e SPRING_DATASOURCE_PASSWORD=ksana_pass \
                  ${IMAGE_NAME}
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Ksana Build & Deployment Successful!"
        }
        failure {
            echo "❌ Ksana Build Failed!"
        }
    }
}