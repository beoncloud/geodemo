pipeline {
  agent any
  stages {
    stage('Maven Jar') {
      steps {
        sh '''
mvn clean install -Pprod -DskipTests'''
      }
    }


  }
  environment {
    tag = '$tag'
  }
}
