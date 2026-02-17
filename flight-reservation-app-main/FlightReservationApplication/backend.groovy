pipeline {
    agent any

    stages {

        stage('Code-pull') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/mayurmwagh/flight-reservation-app.git'
            }
        }

        stage('Build') {
            steps {
                sh '''
                    cd flight-reservation-app-main/FlightReservationApplication
                    mvn clean package
                '''
            }
        }

        stage('QA-Test') {
            steps {
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar') {
                    sh '''
                        cd flight-reservation-app-main/FlightReservationApplication
                        mvn sonar:sonar -Dsonar.projectKey=sonar
                    '''
                }
            }
        }

        stage('Docker-build') {
            steps {
                sh '''
                    cd flight-reservation-app-main/FlightReservationApplication
                    docker build -t aditya986025/flight-reservation-15-16:latest .
                    docker push aditya986025/flight-reservation-15-16:latest
                    docker rmi aditya986025/flight-reservation-15-16:latest
                '''
            }
        }

        stage('Deploy') {
    steps {
        sh '''
            cd flight-reservation-app-main/FlightReservationApplication
            kubectl apply -f k8s/ns.yaml
            kubectl apply -f k8s/
        '''
          }
       }

    }
}
