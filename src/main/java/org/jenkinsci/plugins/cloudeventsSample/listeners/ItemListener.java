package org.jenkinsci.plugins.cloudeventsSample.listeners;

import hudson.model.Item;
import org.jenkinsci.plugins.cloudeventsSample.CurrentStage;

public class ItemListener extends hudson.model.listeners.ItemListener{
    @Override
    public void onCreated(Item item) {
        CurrentStage.CREATED.handleEvent(item, "item");
    }

    @Override
    public void onUpdated(Item item) {
        CurrentStage.UPDATED.handleEvent(item, "item");
    }
}
