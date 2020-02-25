import groovy.json.JsonSlurper 

/*
*This method receives a String form an http request and it obtains the http code and http body
*/
def getStatusAndBody(request){
    http_status = request.toString()[9..11] // we obtain http code
    echo http_status // we display the http code
    if (http_status.toInteger() !=200 && http_status.toInteger() !=201){ //if http code isn't 200 or 201 then there was and error in the request
        http_body = request.toString().substring(128) // we obtain the http body with error
        echo http_body // we display the error
        unstable("ERROR - request status "+http_status) // we mark the pipeline as unstable
    }else{          // if http code is 200 or 201 then the request was successful
        http_body = request.toString().substring(128)  // we obtain the http body
        echo http_body //we display the http body
    }
}

pipeline {
    environment {
        INITDATE='2018-09-23'
        DV8_CONSOLE_IP='dv8-console-tutorial:8080'
        PROJECT_LANGUAGE='java'
        
    }
    agent none
    options {
        skipStagesAfterUnstable()
    }
    stages {
    
    		
        stage('Build') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                sh '${WORKSPACE}/lib_install.sh'
                sh 'mvn -B -DskipTests package'
                script {
	        		env.WORKSPACE="${WORKSPACE}"
	        	}
            }
        } 

        
        
        stage('DV8 analysis') {
        	agent any
            steps {
                /* sh 'curl http://${DV8_CONSOLE_IP}:8080/test-connection 2>/dev/null|jq -r .result' */

                sh 'git log --numstat --date=iso --after=${INITDATE} > ${WORKSPACE}/dv8/dv8gitlog.txt'

                echo "preprocessing files:"
                request_preprocessor= sh(returnStdout: true, script: "curl -i -o - --silent -X GET --header 'Accept: application/json' http://\${DV8_CONSOLE_IP}/preprocessor?directory=\${WORKSPACE}&sourceCodePath=src"); // we make the preprocessor request
                //echo request_preprocessor
                getStatusAndBody(request_processor) // we analyze the preprocessor request 

                echo "generating arch-report:"
                request_arch_report=sh(returnStdout: true, script: "curl -i -o - --silent -X GET --header 'Accept: application/json' http://\${DV8_CONSOLE_IP}/arch-report?directory=\${WORKSPACE}"); // we make the arch report request
                //echo request_arch_report 
                getStatusAndBody(request_arch_report) // we analyze the arch report request

                echo "Propagation cost ="
                request_dl=sh(returnStdout: true, script: "curl -i -o - --silent -X GET --header 'Accept: application/json' 
                http://\${DV8_CONSOLE_IP}/metrics?directory=\${WORKSPACE}'&metric=pc'"); // we make the propagation cost request
                //echo request_dl
                getStatusAndBody(request_dl) // we analyze the decoupling level request

                echo "Decoupling level ="
                request_dl=sh(returnStdout: true, script: "curl -i -o - --silent -X GET --header 'Accept: application/json' 
                http://\${DV8_CONSOLE_IP}/metrics?directory=\${WORKSPACE}'&metric=dl'"); // we make the decoupling level request
                //echo request_dl
                getStatusAndBody(request_dl) // we analyze the decoupling level request

            }
        }
       
        
        stage('Sonarqube analysis') {
            
            agent any
            environment {
                SONAR_SCANNER_OPTS = "-Xmx2g -Dsonar.projectKey=Depends -Dsonar.login=f8028b3c3844933499430df8c40421b3777e09cb -Dsonar.language=${PROJECT_LANGUAGE} -Dsonar.java.binaries=${WORKSPACE}/target/classes -Dsonar.projectBaseDir=${WORKSPACE} -Dsonar.dv8address=${DV8_CONSOLE_IP}"

        		scannerHome = tool 'SonarQubeScanner'
    		}
    		steps {
        		withSonarQubeEnv('SonarQube') {
            		sh "${scannerHome}/bin/sonar-scanner"
        		}

        		timeout(time: 10, unit: 'MINUTES') {
            		waitForQualityGate abortPipeline: true
            	}
            }
        }
    }
    
    post { // these methods are executed at the end of the pipeline, depending on the state in which it ends
        success{ // This method is executed if the pipe is successfully completed.
            echo "All the pipeline steps were performed correctly" 
        }
        failure{ // this method is executed if the pipeline ended with failure
            error "Pipeline failed"
        }
        unstable{ // this method is executed if the pipe ended with unestable
            unstable("Error was found in one of the pipeline steps, please check it")
        }
        always{ // this method always is executed
            echo "Pipeline finished"
        }
    }

}
