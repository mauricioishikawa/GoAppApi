pipeline { 
    agent any

   
    stages { 
        stage("Compilation and Analysis") { 
          steps {
            // parallel 'Compilation': {
//                 sh "sudo fuser -k 443/tcp || true"
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
                sh "mvn spring-boot:run"
              }  
            } 
        } 

  post {
    always {
      cleanWs()
    }
  } 
}