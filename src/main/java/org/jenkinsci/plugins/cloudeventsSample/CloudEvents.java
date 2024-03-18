package org.jenkinsci.plugins.cloudeventsSample;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.util.ArrayList;
import java.util.List;

@Extension
public class CloudEvents extends GlobalConfiguration {
    public static final String PROTOCOL_ERROR_MESSAGE = "Only http and https protocols are supported";

    private String sinkType;
    private String sinkURL;

    private boolean created;
    private boolean updated;
    private boolean started;
    private boolean completed;
    private boolean finalized;
    private boolean failed;



    private List<String> events;

    public CloudEvents() {
        load();
        sinkType = "http";
        sinkURL = "";
        created = true;
        updated = true;
        started = true;
        completed = true;
        finalized = true;
        failed = true;
        events = new ArrayList<>();
    }

    /**
     * @return the singleton instance
     */
    public static CloudEvents get() {
        return GlobalConfiguration.all().get(CloudEvents.class);
    }

    public String getSinkURL() {
        return sinkURL;
    }

    @DataBoundSetter
    public void setSinkURL(String sinkURL) {
        this.sinkURL = sinkURL;
    }

    public String getSinkType() {
        return this.sinkType;
    }

    @DataBoundSetter
    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    public boolean isCreated() {
        return created;
    }

    @DataBoundSetter
    public void setCreated(boolean created) {
        this.created = created;
        addOrRemoveEvent(created, "created");
    }

    public boolean isUpdated() {
        return updated;
    }

    @DataBoundSetter
    public void setUpdated(boolean updated) {
        this.updated = updated;
        addOrRemoveEvent(updated, "updated");
    }


    public boolean isStarted() {
        return started;
    }

    @DataBoundSetter
    public void setStarted(boolean started) {
        this.started = started;
        addOrRemoveEvent(started, "started");
    }

    public boolean isCompleted() {
        return completed;
    }

    @DataBoundSetter
    public void setCompleted(boolean completed) {
        this.completed = completed;
        addOrRemoveEvent(completed, "completed");
    }

    public boolean isFinalized() {
        return finalized;
    }

    @DataBoundSetter
    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
        addOrRemoveEvent(finalized, "finalized");
    }

    public boolean isFailed() {
        return failed;
    }

    @DataBoundSetter
    public void setFailed(boolean failed) {
        this.failed = failed;
        addOrRemoveEvent(failed, "failed");

    }


    public List<String> getEvents() {
        return events;
    }

    private void addOrRemoveEvent(boolean event, String stringEvent) {
        if (event) {
            if (events.contains(stringEvent)) {
                return;
            } else {
                events.add(stringEvent);
            }

        } else {
            if (events.contains(stringEvent)) {
                events.remove(stringEvent);
            }
        }
    }

    public FormValidation doCheckSinkURL(@QueryParameter(value = "sinkURL") String sinkURL) {
        if (sinkURL == null || sinkURL.isEmpty()) {
            return FormValidation.error("Provide valid Sink URL. " +
                    "For ex: \"http://ci.mycompany.com/api/steps\"");
        }
        if (validateProtocolUsed(sinkURL))
            return FormValidation.error(PROTOCOL_ERROR_MESSAGE);
        return FormValidation.ok();

    }

    public boolean validateProtocolUsed(String sinkURL) {
        return !(sinkURL.startsWith("http://") || sinkURL.startsWith("https://"));
    }
}
