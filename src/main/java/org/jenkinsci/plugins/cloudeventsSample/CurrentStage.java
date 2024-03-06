package org.jenkinsci.plugins.cloudeventsSample;

import hudson.model.Run;
import hudson.model.TaskListener;

public enum CurrentStage {
    CREATED, UPDATED, DELETED, STARTED, FINALIZED, COMPLETED;


    public void handleEvent(Object o, String type){

    }

    public void handleEvent(Run run, TaskListener taskListener, long timestamp){

    }
}
