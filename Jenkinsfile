pipeline {
  agent any

  environment {
    DH        = credentials('dockerhub')
    IMAGE     = "jit0924/quick-backend"      
    APP_HOST  = "ubuntu@52.66.243.19"       
    DOCKER_BUILDKIT = '1'
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

    stage('Unit Tests (optional)') {
      when { expression { return false } }   // set to true to enable
      steps { sh 'mvn -B -q -DskipITs test' }
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
            docker build -t ${IMAGE}:${tag} -t ${IMAGE}:latest .
            docker push ${IMAGE}:${tag}
            docker push ${IMAGE}:latest
          """
        }
      }
    }

    stage('Deploy') {
  steps {
    sh '''
      set -e
      # ensure Jenkins can read the files even if owned by ubuntu
      test -r /opt/quick/docker-compose.yml && test -r /opt/quick/.env || true

      echo "$DH_PSW" | docker login -u "$DH_USR" --password-stdin
      docker compose -f /opt/quick/docker-compose.yml --env-file /opt/quick/.env pull backend
      docker compose -f /opt/quick/docker-compose.yml --env-file /opt/quick/.env up -d backend
      docker image prune -f
    '''
  }
}

  post {
    success {
      echo "Deployed ${IMAGE}:latest to ${APP_HOST}"
    }
    always {
      cleanWs()
    }
  }
}
