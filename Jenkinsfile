pipeline {
  agent any
  stages {
    stage('Maven Jar') {
      steps {
        sh '''
mvn clean install -Pprod -DskipTests'''
      }
    }

    stage('BuildImage') {
      steps {
        sh '''echo "#### BUILDING Container Image ###"
pwd
/usr/bin/docker build -t geolocationsvc:1.0.1 .'''
      }
    }

    stage('SonarQube Scanning') {
      steps {
        sh 'mvn sonar:sonar      -Dsonar.host.url=https://sq.intigral-i6.net   -Dsonar.login=80c78e18f182557dca2e12bcc262272c9eb5be9b'
      }
    }

    stage('Pushing to ECR') {
      steps {
        sh '''
/usr/bin/aws ecr get-login-password  --region eu-west-1 | docker login   --username AWS  --password-stdin 667310033456.dkr.ecr.eu-west-1.amazonaws.com
/usr/bin/docker tag geolocationsvc:1.0.1 667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvc:1.0.1
/usr/bin/docker push 667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvc:1.0.1'''
      }
    }

    stage('Deploying to EKS') {
      steps {
        sh '''aws eks --region eu-west-1 update-kubeconfig --name intigral-eks
kubectl config set-context --current --namespace=geolocationsvc
kubectl rollout history deployment/geolocation-deployment
kubectl set image  deployment/geolocation-deployment geolocation-container=667310033456.dkr.ecr.eu-west-1.amazonaws.com/geolocationsvc:1.0.1 --record
kubectl rollout history deployment/geolocation-deployment
kubectl get deployments -o wide
sleep 10
kubectl get pods'''
      }
    }

  }
  environment {
    tag = '$tag'
  }
}