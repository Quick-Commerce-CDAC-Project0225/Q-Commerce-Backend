pipeline {
  agent any

  environment {
    DH              = credentials('dockerhub')       // Docker Hub creds (ID: dockerhub)
    IMAGE           = "jit0924/quick-backend"        // must match docker-compose.yml
    DOCKER_BUILDKIT = '0'                            // disable BuildKit (no buildx needed)
    COMPOSE_IMAGE   = 'docker/compose:2.27.0'        // compose-in-container image
  }

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  triggers {
    githubPush()
  }

  stages {

    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Docker Login') {
      steps {
        sh 'echo "$DH_PSW" | docker login -u "$DH_USR" --password-stdin'
      }
    }

    stage('Build & Push Backend Image') {
      steps {
        script {
          def tag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
          sh """
            set -e
            docker build -t ${IMAGE}:${tag} -t ${IMAGE}:latest .
            docker push ${IMAGE}:${tag}
            docker push ${IMAGE}:latest
          """
        }
      }
    }

    // Jenkins is on the same EC2, so deploy without SSH
    stage('Deploy (local)') {
      steps {
        sh '''
          set -e
          echo "$DH_PSW" | docker login -u "$DH_USR" --password-stdin

          # Run docker compose using the official docker/compose container
          docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -v /opt/quick:/opt/quick \
            -w /opt/quick \
            ${COMPOSE_IMAGE} \
            --env-file /opt/quick/.env pull backend

          docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -v /opt/quick:/opt/quick \
            -w /opt/quick \
            ${COMPOSE_IMAGE} \
            --env-file /opt/quick/.env up -d backend

          docker image prune -f
        '''
      }
    }
  }

  post {
    success {
      echo "✅ Deployed ${IMAGE}:latest to local EC2 via docker compose"
    }
    always {
      cleanWs()
    }
  }
}
