package org.jenkinsci.plugins.cloudeventsSample;


import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.BulkChange;
import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.ManagementLink;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import hudson.security.Permission;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import org.jfree.util.Log;
import org.kohsuke.stapler.*;
import org.kohsuke.stapler.interceptor.RequirePOST;
import org.kohsuke.stapler.verb.POST;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.logging.Level;

import static hudson.model.Items.getConfigFile;

@Extension
public class CloudEvents extends ManagementLink implements Saveable, Describable<CloudEvents> {

    private String sinkType;

    public static final String PROTOCOL_ERROR_MESSAGE = "Only http and https protocols are supported";
    private String sinkURL;

    private boolean created;
    private boolean updated;
    private boolean enteredWaiting;
    private boolean left;
    private boolean started;
    private boolean completed;
    private boolean finalized;
    private boolean failed;
    private boolean online;
    private boolean offline;

    private List<String> events;

    private static final Logger LOG = Logger.getLogger(CloudEvents.class.getName());

    @DataBoundConstructor
    public CloudEvents(){
        sinkType = "http";
        sinkURL = "";
        created = true;
        updated = true;
        enteredWaiting = true;
        left = true;
        started = true;
        completed = true;
        finalized = true;
        failed = true;
        online = true;
        offline = true;
        events = new ArrayList<>();
        load();
    }

    @Override
    public String getIconFileName() {
        return "clipboard.png";
    }

    @Override
    public String getUrlName() {
        return "cloudevents";
    }

    @Override
    public String getDisplayName() {
        return "CloudEvents";
    }

    @Override
    public String getDescription() {
        return "Send Jenkins events to a CloudEvents sink";
    }

    public @NonNull String getCategoryName() {
        return "CONFIGURATION";
    }

    @NonNull
    @Override
    public Permission getRequiredPermission() {
        return Jenkins.ADMINISTER;
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

    public boolean isEnteredWaiting() {
        return enteredWaiting;
    }

    @DataBoundSetter
    public void setEnteredWaiting(boolean enteredWaiting) {
        this.enteredWaiting = enteredWaiting;
        addOrRemoveEvent(enteredWaiting, "entered_waiting");
    }

    public boolean isLeft() {
        return left;
    }

    @DataBoundSetter
    public void setLeft(boolean left) {
        this.left = left;
        addOrRemoveEvent(left, "left");
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

    public boolean isOnline() {
        return online;
    }

    @DataBoundSetter
    public void setOnline(boolean online) {
        this.online = online;
        addOrRemoveEvent(online, "online");
    }

    public boolean isOffline() {
        return offline;
    }

    @DataBoundSetter
    public void setOffline(boolean offline) {
        this.offline = offline;
        addOrRemoveEvent(offline, "offline");
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

    public synchronized void load() {
        XmlFile file = getConfigFile();
        if (!file.exists()) {
            return;
        }

        try {
            file.unmarshal(this);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to load " + file, e);
        }
    }

    @Override
    public synchronized void save() {
        if (BulkChange.contains(this)) {
            return;
        }
        try {
            getConfigFile().write(this);
            SaveableListener.fireOnChange(this, getConfigFile());
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to save " + getConfigFile(), e);
        }
    }

    private XmlFile getConfigFile() {
        return new XmlFile(new File(Jenkins.get().getRootDir(), getClass().getName() + ".xml"));
    }


    @Extension
    public static final class DescriptorImpl extends Descriptor<CloudEvents>{
        public DescriptorImpl(){
            super.load();
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

    @Override
    public Descriptor<CloudEvents> getDescriptor(){
        return Jenkins.get().getDescriptorOrDie(getClass());
    }

    @RequirePOST
    public void doconfigSubmit(StaplerRequest req, StaplerResponse res) throws IOException {
        res.sendRedirect("../");
    }
}
