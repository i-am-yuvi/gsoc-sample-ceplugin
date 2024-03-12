package org.jenkinsci.plugins.cloudeventsSample;

import hudson.EnvVars;
import hudson.model.*;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.cloudeventsSample.Sinks.HTTPSink;
import org.jenkinsci.plugins.cloudeventsSample.Sinks.KafkaSink;
import org.jenkinsci.plugins.cloudeventsSample.models.BuildModel;
import org.jenkinsci.plugins.cloudeventsSample.models.JobModel;
import org.jenkinsci.plugins.cloudeventsSample.models.ScmState;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum CurrentStage {
    CREATED, UPDATED, DELETED, STARTED, FINALIZED, COMPLETED;

    private static final Logger LOGGER = Logger.getLogger("CurrentStage");


    public void handleEvent(Object o, String type){

    }

    public void handleEvent(Run run, TaskListener taskListener, long timestamp) throws NullPointerException{
        String sinkUrl = CloudEvents.get().getSinkURL();

        for(String event : CloudEvents.get().getEvents()) {
            if checkBuild(event, run.getResult()) {

                try {
                    JobModel jobModel = buildJobModel(run.getParent(), run, timestamp, taskListener);

                    jobModel.setStage(event);

                    switch (CloudEvents.get().getSinkType()){
                        case "http":
                            HTTPSink httpsink = new HTTPSink();
                            httpsink.sendCloudEvent(sinkURL, jobModel);
                            break;
                        case "kafka":
                            KafkaSink kafkaSink = new KafkaSink();
                            kafkaSink.sendCloudEvent(sinkURL, jobModel);
                            break;
                        default:
                            break;
                        }

                    } catch (Throwable error){
                    Logger.log(Level.WARNING, "Failed to notify the Sink. Error: " + error.getMessage());
                }


            }
        }
    }

    public JobModel buildJobModel(Job parent, Run run, long timestamp, TaskListener taskListener) throws IOException, InterruptedException{

        String rootUrl = Jenkins.get().getRootUrl();

        JobModel jobModel = new JobModel();

        jobModel.setName(parent.getName());
        jobModel.setDisplayName(parent.getDisplayName());
        jobModel.setUrl(parent.getUrl());

        String username = Jenkins.getAuthentication2().getCredentials().toString();

        User user = Jenkins.get().getUser(username);
        if (user != null){
            jobModel.setUserId(user.getId());
            jobModel.setUserName(user.getFullName());
        }

        Result result = run.getResult();

        BuildModel buildModel = new BuildModel();
        buildModel.setNumber(run.number);
        buildModel.setQueueId(run.getQueueId());
        buildModel.setUrl(run.getUrl());
        buildModel.setPhase(this);
        buildModel.setTimestamp(timestamp);
        buildModel.setDuration(run.getDuration());

        ParametersAction parametersAction = run.getAction(ParametersAction.class);
        if (parametersAction != null){
            EnvVars envVars = new EnvVars();
            for(ParameterValue parameterValue : parametersAction.getParameters()){
                if (!parameterValue.isSensitive()) {
                    parameterValue.buildEnvironment(run, envVars);
                }
            }
            buildModel.setParameters(envVars);
        }

        jobModel.setBuild(buildModel);

        EnvVars envVars = run.getEnvironment(taskListener);

        ScmState scmState = new ScmState();
        if (envVars.get("GIT_URL") != null){
            if (envVars.get("GIT_URL") != null){
                scmState.setUrl(envVars.get("GIT_URL"));
            }
            if(envVars.get("GIT_BRANCH") != null){
                scmState.setBranch(envVars.get("GIT_BRANCH"));
            }
            if(envVars.get("GIT_COMMIT") != null){
                scmState.setCommit(envVars.get("GIT_BRANCH"));
            }
        }

        buildModel.setScmState(scmState);

        if (result != null){
            buildModel.setStatus(result.toString());
        }

        if (rootUrl != null){
            buildModel.setUrl(rootUrl + run.getUrl());
        }

        return jobModel;
    }

    public JobModel buildJobModel(Item item) throws IOException{

        JobModel jobModel = new JobModel();

        jobModel.setName(item.getName());
        jobModel.setDisplayName(item.getDisplayName());

        String userName = Jenkins.getAuthentication2().getName();
        User user = Jenkins.get().getUser(userName);

        if (user != null){
            jobModel.setUserId(user.getId());
            jobModel.setUserName(user.getFullName());
        }

        jobModel.setUrl(item.getUrl());

        switch (this){
            case CREATED:
                jobModel.setCreatedDate(new Date());
                jobModel.setStatus("CREATED");
                break;

            case UPDATED:
                jobModel.setUpdatedDate(new Date());
                jobModel.setStatus("UPDATED");
                break;

            case DELETED:
                jobModel.setDeletedDate(new Date());
                jobModel.setStatus("DELETED");
                break;

            default:
                jobModel.setStatus(null);
                break;
        }

        AbstractProject<?, ?> project = (AbstractProject<?, ?>) item;
        jobModel.setConfigFile(project.getConfigFile().toString());

        return jobModel;

    }

    //sending events related to run of a job

    public boolean shouldSendBuild(String event, Result result){

        switch (event){
            case "failed":
                if (result == null){
                    return false;
                }
                return this.equals(FINALIZED) && result.equals(Result.FAILURE);

            default:
                return event.equals(this.toString().toLowerCase());
        }
    }
}
