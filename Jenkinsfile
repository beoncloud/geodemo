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

        stage('SonarQube Scanning') {
          steps {
            sh 'mvn sonar:sonar      -Dsonar.host.url=https://sq.intigral-i6.net   -Dsonar.login=0c294a875e41aff9c1880669f1b4f8fa185b600f'
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

/usr/bin/docker tag geolocationsvcstg:demo 667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvcstg:demo

/usr/bin/docker push 667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvcstg:demo'''
      }
    }

    stage('Deploying to EKS') {
      steps {
        sh '''kubectl config set-context --current --namespace=geolocation-service-demo

kubectl rollout history deployment/geolocation-deployment-demo

kubectl set image  deployment/geolocation-deployment-demo geolocation-container-demo=667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvcstg:demo --record

kubectl rollout history deployment/geolocation-deployment-demo

kubectl get deployments -o wide

sleep 10

kubectl get pods | grep demo'''
      }
    }

  }
}