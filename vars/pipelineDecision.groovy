#!groovy
def decidepipeline(Map configMap){
    application = configMap.get("application")
    switch(application) {
        case 'nodejsVM':
            nodejsVM(configMap)
            break
        case 'javaVM':
            javaVM(configMap)
            break
        case 'nodejsEKS':
            nodejsEKS(configMap)
            break
        default:
          error "application is not recognised"
          break
    }
}