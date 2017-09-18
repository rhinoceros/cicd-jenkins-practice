import groovy.json.JsonSlurper
def slurper = new JsonSlurper()
def jsonText= readFileFromWorkspace("cd/config.json")
products = slurper.parseText(jsonText)
products.each { product ->
  //println "product.name:"+product.name
  nestedView(product.name) {
    views {
      product.stages.each{stage ->
        views{
          println "stage:"+stage
          listView(stage) {
            jobs {
              //println "${product.name}__.*-to-${stage}__.*"
              regex(/${product.name}__.*-to-${stage}_*.*/)
            }
            columns {
              status()
              weather()
              name()
              lastSuccess()
              lastFailure()
            }
          }
        }
      }
      buildMonitorView('BM_'+product.name) {
            description('All jobs for product '+product.name)
            jobs {
                name('release-projectA')
                regex(/${product.name}__.+/)
            }
      }
    }
  }
}
