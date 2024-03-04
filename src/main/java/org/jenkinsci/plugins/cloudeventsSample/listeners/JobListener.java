package org.jenkinsci.plugins.cloudeventsSample.listeners;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.model.Run;
import org.jenkinsci.plugins.cloudeventsSample.CurrentStage;

@Extension
public class JobListener extends RunListener<Run> {
    public JobListener() {
        super(Run.class);
    }

    @Override
    public void onCompleted(Run run, @NonNull TaskListener listener) {
        CurrentStage.COMPLETED.handleEvent(run, listener, run.getTimeInMillis() + run.getDuration());
    }

    @Override
    public void onFinalized(Run run) {
        CurrentStage.FINALIZED.handleEvent(run, TaskListener.NULL, System.currentTimeMillis());
    }

    @Override
    public void onStarted(Run run, TaskListener listener) {
        CurrentStage.STARTED.handleEvent(run, listener, run.getTimeInMillis());
    }

}
