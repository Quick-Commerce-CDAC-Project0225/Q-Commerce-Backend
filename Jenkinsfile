pipeline {
  agent any
  environment {
    DH = credentials('dockerhub')
    IMAGE = "${DH_USR}/quick-backend"
    APP_HOST = "ubuntu@52.66.243.19" 
    DOCKER_BUILDKIT = '1'
  }
  options { timestamps(); disableConcurrentBuilds() }
  triggers { githubPush() }

  stages {
    stage('Checkout'){ steps { checkout scm } }

   
    stage('Unit Tests'){
      steps { sh 'mvn -B -q -DskipITs test' }
    }

    stage('Docker Login'){
      steps { sh 'echo "$DH_PSW" | docker login -u "$DH_USR" --password-stdin' }
    }

    stage('Build & Push Image'){
      steps {
        script {
          def tag = sh(returnStdout:true, script:'git rev-parse --short HEAD').trim()
          sh """
            docker build -t ${IMAGE}:${tag} -t ${IMAGE}:latest .
            docker push ${IMAGE}:${tag}
            docker push ${IMAGE}:latest
          """
        }
      }
    }

    stage('Deploy'){
      steps {
        sshagent(credentials:['ec2-ssh']) {
          sh """
            ssh -o StrictHostKeyChecking=no ${APP_HOST} '
              cd /opt/quick &&
              docker login -u ${DH_USR} -p ${DH_PSW} &&
              docker compose pull backend &&
              docker compose up -d backend &&
              docker image prune -f
            '
          """
        }
      }
    }
  }
  post { success { echo "Deployed ${IMAGE}:latest" } always { cleanWs() } }
}
