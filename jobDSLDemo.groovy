import groovy.json.JsonSlurper
import jenkins.*
def slurper = new JsonSlurper()
def jsonText= readFileFromWorkspace("cd/config.json")
products = slurper.parseText(jsonText)


products.each { product ->

	def product_from_to_list = []
	def project_from_to_list = []

	product.projects.each{project ->
		project.pipelines.each{pipeline ->
			if ([pipeline.from, pipeline.to ]in product_from_to_list) {
				println "[INFO] already in product_from_to_list"
			} else {
				println "[INFO] add to product_from_to_list: [pipeline.from, pipeline.to] "
				product_from_to_list.add([pipeline.from, pipeline.to])
			}

			//println "${product.name}__.*-to-${stage}__.*"
			//regex(/${product.name}__.*-to-${stage}__.*/)
			job_name="${product.name}__${pipeline.from}-to-${pipeline.to}__${project.name}"
			repo_url="git@YOUR_GITLAB_SERVER:${product.name}/${project.name}.git"
			println "[INFO] ${product.name}/${project.name} --> ${job_name}--> ${repo_url}"
			build_shell='''#!/bin/bash
bash  /opt/cicd/com/company/main.sh
'''

			repo_url_http="http://YOUR_GITLAB_URL/${product.name}/${project.name}.git"
			project_from_to_list.add(job_name)
			job(job_name)  {
				authenticationToken('YOUR_JOB_AUTH_TOKEN')
				if(project?.label != null)
				{
					label(project?.label)
				}
				parameters {
					gitParam('BRANCH_NAME') {
						type('BRANCH')
						description('input the branchname you want to deploy')
						branch('master')
					}
				}
				scm{
					git{
						remote{
							url(repo_url)
							credentials('gitlab-cicd-key')
						}
						branches('origin/${gitlabSourceBranch}','${BRANCH_NAME}')
						browser { gitLab(repo_url_http, '9.4') }
					}
				}
				triggers {
					gitlabPush {
						buildOnMergeRequestEvents(false)
						buildOnPushEvents(true)
						enableCiSkip(true)
						setBuildDescription(true)
						rebuildOpenMergeRequest('never')
						includeBranches('test,develop')

					}
				}
				steps { shell(build_shell) }

   // demo for configure : using configure config jenkins.plugins.bearychat.BearychatNotifier
configure { project_bearychat ->
        project_bearychat / publishers << 'jenkins.plugins.bearychat.BearychatNotifier' {
teamDomain ''
authToken ''
buildServerUrl "http://YOUR_JENINS_SERVER_URL/"
room ''
startNotification true
notifySuccess true true
notifyAborted true
notifyNotBuilt true
notifyUnstable true
notifyFailure true
notifyBackToNormal true
notifyRepeatedFailure false
includeBearychatCustomMessage false
bearychatCustomMessage ''
bearychatEndCustomMessage ''

}
}

			}
		}
	}

	product_from_to_list.each{ from,to ->
		jobsJoinedStr=project_from_to_list.findAll{it.contains("${product.name}__${from}-to-${to}__")}.collect{"'"+it+"'"}.join(' , ')
		script_text='project_list = ['+ jobsJoinedStr +']'
		script_text=script_text+'''
    node {
      echo 'start build'
      for(int i=0;i<project_list.size();i++) {
        project_name=project_list[i]
        stage (project_name) {
          build job: "${project_name}", parameters: [
            [$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "${BRANCH_NAME}"],
          ]
        }
      }
    }
    '''

		jobname="${product.name}__${from}-to-${to}"
		pipelineJob(jobname) {
            parameters {
                stringParam('BRANCH_NAME', 'master', 'BRANCH NAME')
            }
			definition {
				cps {
					script(script_text)
					sandbox()
				}
			}
		}
	}






}
