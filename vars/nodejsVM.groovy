def call(Map configMap){
  pipeline {
    agent {
        node {
            label 'Agent-1'
        }
    }
    environment {
        packageversion = ''
    
    }
    //some crumb error is coming in deploy stage by adding deploy parameter
    options {
        timeout(time: 1 , unit : 'HOURS')
        disableConcurrentBuilds()
        ansiColor('xterm')
    }
     parameters {
            booleanParam(name: 'Deploy', defaultValue: false, description: 'Toggle this value')
            }
    stages {
        stage('get version') { 
            steps {
                script {
                    def PackageJson = readJSON file: 'package.json'
                    packageversion = PackageJson.version
                    echo "application version: $packageversion"
                } 
            }
        }
        stage('install dependancies') { 
            steps {
                sh """
                  npm install
                """  
            }
        }
        stage('unit testing') { 
            steps {
                sh """
                  echo "unit test cases will run here"
                """  
            }
        }
        stage('sonar scan') { 
            steps {
                sh """
                  echo "sonar scanning done"
                """  
            }
        }

        stage('build') { 
            steps {
                sh """
                  ls -la
                  zip -q -r ${configMap.component}.zip ./* -x ".git" -x "*.zip"
                  ls -ltr
                """  
            }
        }
        stage('publish artifacts'){
            steps{
                nexusArtifactUploader(
                   nexusVersion: 'nexus3',
                   protocol: 'http',
                   nexusUrl: pipelineGlobals.nexusURL(),
                   groupId: 'com.roboshop',
                   version: "${packageversion}",
                   repository: "${configMap.component}",
                   credentialsId: 'nexus-auth',
                   artifacts: [
                      [artifactId: "${configMap.component}",
                       classifier: '',
                       file: "${configMap.component}.zip",
                       type: 'zip']
        ]
     )
            }
        }
        stage('deploy'){
            when {
                    expression{
                        params.Deploy
                    }
                }
            steps{
                script{
                    def params = [
                        string(name:'version', value: "$packageversion"),
                        string(name:'environment', value: "dev")
                    ]
                    build job: "../${configMap.component}-deploy" , wait: true , parameters: params
                }
            }
        }
    }
    post {
        always {
            echo " i will always say hello again"
            deleteDir()
        }
        failure {
            echo " this runs when pipeline is failed , used generally to send some alerts"
        }
        success {
            echo " i will say hello when pipeline is success"
        }
    }
}

}
 