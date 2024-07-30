def call(map configMap){
  pipeline {
    agent {
        node {
            label 'Agent-1'
        }
    }
    environment {
        packageversion = ''
        nexusURL = '3.86.47.135:8081'
    }
    //some crumb error is coming in deploy stage by adding deploy parameter
    options {
        timeout(time: 1 , unit : 'HOURS')
        disableConcurrentBuilds()
        ansiColor('xterm')
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
    }
 }
}