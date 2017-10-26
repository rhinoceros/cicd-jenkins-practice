import hudson.model.*
import jenkins.model.*
import hudson.slaves.*
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry


println 'https://support.cloudbees.com/hc/en-us/articles/218154667-Create-a-Permanent-Agent-from-Groovy-Console'
println 'https://support.cloudbees.com/hc/en-us/articles/222520647-How-to-find-slave-secret-key-'
ComputerLauncher jnlpLauncher=new JNLPLauncher()
// INSERT "Launch Method" SNIPPET HERE

// Define a "Permanent Agent"
Slave agent = new DumbSlave(
        "agent-node",
        "/home/jenkins",
        jnlpLauncher)
agent.nodeDescription = "Agent node description"
agent.numExecutors = 1
agent.labelString = "agent-node-label"
agent.mode = Node.Mode.EXCLUSIVE
agent.retentionStrategy = new RetentionStrategy.Always()
List<Entry> env = new ArrayList<Entry>();
env.add(new Entry("key1","value1"))
env.add(new Entry("key2","value2"))
env.add(new Entry("CLASSPATH","/opt/jdk1.7.0_17/lib:/opt/jdk1.7.0_17/jre/lib:."))
env.add(new Entry("JAVA_HOME","/opt/jdk1.7.0_17"))
env.add(new Entry("JRE_HOME","/opt/jdk1.7.0_17/jre"))
env.add(new Entry("PATH","/opt/apache_maven/apache-maven-3.0.5/bin:/opt/jdk1.7.0_17/bin:/opt/jdk1.7.0_17/jre/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"))
EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(env);
agent.getNodeProperties().add(envPro)

// Create a "Permanent Agent"
Jenkins.instance.addNode(agent)

return "Node has been created successfully."



