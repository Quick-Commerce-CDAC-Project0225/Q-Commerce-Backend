pipeline {
  agent any

  environment {
    DH              = credentials('dockerhub')       // Jenkins credential ID: dockerhub
    IMAGE           = "jit0924/quick-backend"
    DOCKER_BUILDKIT = '0'                            // classic builder (no buildx)
    COMPOSE_IMAGE   = 'docker/compose:2'             // run compose via container
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

    // Deploy on same EC2 using docker/compose container (no plugin needed)
    stage('Deploy (local)') {
      steps {
        sh '''
          set -e
          echo "$DH_PSW" | docker login -u "$DH_USR" --password-stdin

          # Ensure compose image present
          docker pull ${COMPOSE_IMAGE} || true

          # Pull and restart BACKEND
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

          # (Optional) also refresh FRONTEND if present
          docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -v /opt/quick:/opt/quick \
            -w /opt/quick \
            ${COMPOSE_IMAGE} \
            --env-file /opt/quick/.env pull frontend || true

          docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -v /opt/quick:/opt/quick \
            -w /opt/quick \
            ${COMPOSE_IMAGE} \
            --env-file /opt/quick/.env up -d frontend || true

          docker image prune -f
        '''
      }
    }
  }

  post {
    success {
      echo "✅ Deployed ${IMAGE}:latest (and frontend) via docker/compose container"
    }
    always {
      cleanWs()
    }
  }
}
