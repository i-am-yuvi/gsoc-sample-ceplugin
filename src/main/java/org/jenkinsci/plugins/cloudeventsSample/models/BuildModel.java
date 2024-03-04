package org.jenkinsci.plugins.cloudeventsSample.models;

import org.jenkinsci.plugins.cloudeventsSample.CurrentStage;

import java.util.Map;

public class BuildModel {
    private String fullUrl;

    private int number;

    private long queueId;

    private long timestamp;

    private long duration;

    private CurrentStage phase;

    private String status;

    private String url;

    private String displayName;

    private Map<String, String> parameters;

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException();
        }
        this.number = number;
    }

    public long getQueueId() {
        return queueId;
    }

    public void setQueueId(long queueId) {
        if (queueId <= 0) {
            throw new IllegalArgumentException();
        }
        this.queueId = queueId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public CurrentStage getPhase() {
        return phase;
    }

    public void setPhase(CurrentStage phase) {
        this.phase = phase;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

}
