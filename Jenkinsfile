node { 
    agent any

   
    stages { 
        stage("Compilation and Analysis") { 
            parallel 'Compilation': {
                sh "sudo fuser -k 443/tcp || true" 
                sh "mvn clean install -DskipTests" 
            } 
        } 
         
        stage("Tests and Deployment") { 
            parallel 'Unit tests': { 
                stage("Runing unit tests") { 
                    sh "mvn test -Punit" 
                } 
            } 
             
            stage("Staging") { 
                sh "sudo mvn spring-boot:run"     
            } 
        } 
    }
    
  post {
    always {
      cleanWs()
    }
  } 
}