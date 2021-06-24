pipeline {
  agent any
  stages {
    stage('Building By Maven') {
      steps {
        sh '''
mvn clean install -Pprod -DskipTests'''
      }
    }

    stage('Deploy') {
      steps {
        sh 'echo "Deploying"'
      }
    }

  }
  environment {
    tag = '$tag'
  }
}