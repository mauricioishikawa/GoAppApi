pipeline { 
    agent any

   
    stages { 
        stage("Compilation and Analysis") { 
          steps {
            // parallel 'Compilation': {
                sh "fuser -k 8081/tcp || true"
                sh "mvn clean install -DskipTests"
          } 
            // }
        } 
         
        // stage("Tests and Deployment") { 
        //     parallel 'Unit tests': { 
//                 stage("Runing unit tests") {
//                   steps {
//                     sh "mvn test -Punit"
//                   }
//                 }
            
             
            stage("Staging") { 
              steps {
                sh "java -jar ./target/go-0.0.1-SNAPSHOT.jar &"
              }  
            } 
        } 

//   post {
//     always {
//       cleanWs()
//     }
//   }
}