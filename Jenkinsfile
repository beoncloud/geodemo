pipeline {
  agent any
  stages {
    stage('Maven Jar') {
      steps {
        sh 'mvn clean install -Pprod -DskipTests'
      }
    }

    stage('BuildImage') {
      steps {
        sh '''echo "#######################################/n BUILDING Container Image /n #################################"
pwd
/usr/bin/docker build -t geolocationsvcstg:demo .'''
      }
    }

    stage('Unit Testing') {
      parallel {
        stage('Unit Testing') {
          steps {
            sh 'echo "Test 1"'
          }
        }

        stage('test1') {
          steps {
            sh 'echo "Test 2"'
          }
        }

        stage('test2') {
          steps {
            sh 'echo "test3"'
          }
        }

        stage('test3') {
          steps {
            sh 'echo "testing"'
          }
        }

      }
    }

    stage('Pushing to ECR') {
      steps {
        sh '''

/usr/bin/aws ecr get-login-password  --region eu-west-1 | docker login   --username AWS  --password-stdin 667310033456.dkr.ecr.eu-west-1.amazonaws.com

/usr/bin/docker tag geolocationsvcstg:$1 667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvcstg:$1

/usr/bin/docker push 667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvcstg:demo'''
      }
    }

  }
}