import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

import hudson.scm.*
import hudson.tasks.*
import com.cloudbees.hudson.plugins.folder.*
in_view_jobs=[]

def showJobInfo(view1, view2, input_job) {
  if(input_job instanceof com.cloudbees.hudson.plugins.folder.Folder ) return;

  def  f_build=input_job.getLastFailedBuild()
  def  s_build=input_job.getLastSuccessfulBuild()
  f_build_time=f_build?.getTime()?.format("YYYY-MMM-dd")
  s_build_time=s_build?.getTime()?.format("YYYY-MMM-dd")
  info=[
        view1,
        view2,
        input_job.name,
        input_job.disabled,
        input_job.assignedLabel,
        f_build_time,
        s_build_time
      ].join('\t')
  println info
}

Jenkins.instance.getViews().each{
  view1=it.name
  if(it instanceof AllView) return;

  if(it instanceof ListView) {
    it.items.each(){input_job->
      view2="view2"
      in_view_jobs.add(input_job.name)
      showJobInfo(view1,view2,input_job)
    }
  }
  else{
    it.getViews().each{
      view2=it.name
      it.items.each{input_job->
        in_view_jobs.add(input_job.name)
        showJobInfo(view1,view2,input_job)
      }
    }
  }
}

Jenkins.items.each{
  if(it.name in in_view_jobs)  return
    in_view_jobs.add(it.name)
  showinfo("view1","view2",it)
}

""

