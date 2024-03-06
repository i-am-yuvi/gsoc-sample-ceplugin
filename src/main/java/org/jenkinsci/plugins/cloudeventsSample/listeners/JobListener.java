package org.jenkinsci.plugins.cloudeventsSample.listeners;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import org.jenkinsci.plugins.cloudeventsSample.CurrentStage;

import javax.annotation.Nonnull;
import java.sql.Time;

public class JobListener extends RunListener<Run> {

    @Override
    public void onStarted(Run run, TaskListener taskListener){
        CurrentStage.STARTED.handleEvent(run, taskListener, run.getTimeInMillis());
    }

    @Override
    public void onFinalized(Run run){
        CurrentStage.FINALIZED.handleEvent(run, TaskListener.NULL, System.currentTimeMillis());
    }

    @Override
    public void onCompleted(Run run, @Nonnull TaskListener taskListener){
        CurrentStage.COMPLETED.handleEvent(run, taskListener, run.getTimeInMillis() + run.getDuration());
    }
}
