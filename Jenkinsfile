pipeline {
    environment {
        INITDATE='2018-09-23'
        DV8_CONSOLE_IP='dv8-console_tutorial:8080'
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
                sh 'lib_install.sh'
                sh 'mvn -B -DskipTests clean package'
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
                sh 'java -jar /depends.jar -s -p dot -d ${WORKSPACE}/dv8/ java . dependency'
                

                echo "generating arch-report:"
                sh 'curl http://${DV8_CONSOLE_IP}/arch-report?directory=${WORKSPACE}'

                echo "Propagation cost ="
                echo sh(returnStdout: true, script: 'curl -X POST http://${DV8_CONSOLE_IP}/metrics -d "directory=${WORKSPACE}&metric=pc" 2>/dev/null')
                
                echo "Decoupling level ="
                echo sh(returnStdout: true, script: 'curl -X POST http://${DV8_CONSOLE_IP}/metrics -d "directory=${WORKSPACE}&metric=dl" 2>/dev/null')

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
}
