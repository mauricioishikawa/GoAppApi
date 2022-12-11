pipeline { 
    agent any

    environment {
        dockerhub=credentials('dockerhub')
    }

    stages { 
        stage("Compilation and Analysis") { 
          steps {
            // parallel 'Compilation': {
                sh "fuser -k 8081/tcp || true"
                sh "mvn clean install -DskipTests"
          } 
            // }
        }

        stage("Deploy") {
            steps {
                sh "mvn package -DskipTests"
            }
        }
        stage("Build Docker Image") {
            steps {
                sh 'docker build -t mauishikawa/docker_jenkins_springboot:${BUILD_NUMBER} .'
            }
        }
        stage("Docker login") {
            steps {
//                 withCredentials([string(credentialsId: 'DockerId', variable: 'Dockerpwd')]) {
                    sh "echo $dockerhub_PSW | docker login -u $dockerhub_USR --password-sttdin"
//                 }
            }
        }
        stage("Docker push") {
            steps {
                sh 'docker push mauishikawa/docker_jenkins_springboot:${BUILD_NUMBER}'
            }
        }
        stage("Docker deploy") {
            steps {
                sh 'docker run -itd -p 8081:8081 mauishikawa/docker_jenkins_springboot:${BUILD_NUMBER}'
            }
        }
        stage("Archiving") {
             steps {
                 archiveArtifacts '**/target/*.jar'
             }
        }
    }
}



         
        // stage("Tests and Deployment") { 
        //     parallel 'Unit tests': { 
//                 stage("Runing unit tests") {
//                   steps {
//                     sh "mvn test -Punit"
//                   }
//                 }
            
             
//             stage("Staging") {
//               steps {
//                 sh "nohup mvn spring-boot:run &"
//               }
//             }
//         }

//   post {
//     always {
//       cleanWs()
//     }
//   }
// }