# -*- coding: utf-8 -*-
"""
gitlab project pushed hooks: jenkins bearychat
"""
import sys
import json
from optparse import OptionParser
import gitlab

def set_pushed_hook(gl_url, gl_token, jenkins_url, job_token):
    '''set_pushed_hook'''
    gl = gitlab.Gitlab(gl_url, gl_token, api_version=4)
    gl.auth()
    with open('config.json', 'r') as data_file:
        data = json.load(data_file)
        for product in data:
            if product['name'] == "JAM":
                group_name = product['name']
                for project in product['projects']:
                    project_name = project['name']
                    gl_project = gl.projects.get(group_name+'/'+project_name)
                    job_url = jenkins_url+'/project/'+group_name+'__BUILD-to-CI__'+project_name
                    hooks = gl_project.hooks.list()
                    for h in hooks:
                        print h.url
                    ci_hook = next((h for h in hooks if h.url == job_url), None)
                    if ci_hook is not None:
                        ci_hook.delete()
                    ci_hook = gl.project_hooks.create({'url': job_url,
                                                       'push_events': 1,
                                                       'enable_ssl_verification': 0,
                                                       'token':job_token},
                                                      project_id=gl_project.id)
                    #chat_url = 'https://hook.bearychat.com/*********/gitlab/*****************'
                    #bearychat_hook = next((h for h in hooks if h.url == chat_url), None)
                    #if bearychat_hook is not None:
                    #    bearychat_hook.delete()
                    #bearychat_hook = gl.project_hooks.create({'url': chat_url,
                    #                                          'push_events': 1,
                    #                                          'enable_ssl_verification': 1},
                    #                                         project_id=gl_project.id)


if __name__ == "__main__":
    OPT_PARSE = OptionParser()
    OPT_PARSE.add_option("-g", "--gl_url", dest="gl_url", help="gitlab server url")
    OPT_PARSE.add_option("-t", "--gl_token", dest="gl_token", help="gitlab auth token")
    OPT_PARSE.add_option("-j", "--jenkins_url", dest="jenkins_url", help="jenkins server url")
    OPT_PARSE.add_option("-a", "--job_token", dest="job_token", help="jenkins Job AuthToken")
    OPTIONS, ARGS = OPT_PARSE.parse_args()

    if (not OPTIONS.gl_url) \
        or (not OPTIONS.gl_token) \
        or (not OPTIONS.jenkins_url) \
        or (not OPTIONS.job_token):
        OPT_PARSE.print_help()
        sys.exit()

    set_pushed_hook(OPTIONS.gl_url,
                    OPTIONS.gl_token,
                    OPTIONS.jenkins_url,
                    OPTIONS.job_token)
