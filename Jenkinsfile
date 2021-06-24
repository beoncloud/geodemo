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
        sh 'java -Dmmdb_ip=GeoLite2-Country-Blocks-IPv4.csv -Dmmdb_country=GeoLite2-Country-Locations-en.csv -jar target/geolocation-0.1.0.jar'
      }
    }

  }
  environment {
    tag = '$tag'
  }
}