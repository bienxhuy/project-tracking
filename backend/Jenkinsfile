pipeline {
  agent any

  environment {
    IMAGE_NAME = 'letmehear/blog-app'
    IMAGE_TAG = 'latest'
  }

  stages {
    stage('Clone source') {
      steps {
        git 'https://github.com/nguyentruong0904/Comic-Review-Blog-BE.git'
      }
    }

    stage('Prepare .env') {
      steps {
        withCredentials([file(credentialsId: 'blog-comic-env-file', variable: 'ENV_FILE')]) {
         sh '''
           rm -f .env
           cp $ENV_FILE .env
         '''
        }
      }
    }

    stage('Check env') {
      steps {
        sh '''
          echo "JAVA_HOME=$JAVA_HOME"
          echo "PATH=$PATH"
          which native-image || echo "native-image NOT found"
        '''
      }
    }

    stage('Build Native Image') {
      steps {
//         sh '''
//           bash -c "set -a && source .env && set +a && ./mvnw -Pnative native:compile -DskipTests"
//         '''
        sh '''
          bash -c "set -a && source .env && set +a && ./mvnw package -DskipTests"
        '''
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
      }
    }

    stage('Push to Docker Hub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
          sh '''
            echo "$PASSWORD" | docker login -u "$USERNAME" --password-stdin
            docker push $IMAGE_NAME:$IMAGE_TAG
          '''
        }
      }
    }

    stage('Notify or Trigger Render') {
      steps {
        echo 'Image pushed. Go to Render to update (or trigger deploy via API).'
      }
    }
  }
}
