package org.jenkinsci.plugins.cloudeventsSample.listeners;

import hudson.Extension;
import hudson.model.Item;
import org.jenkinsci.plugins.cloudeventsSample.CurrentStage;

@Extension
public class ItemsListener extends hudson.model.listeners.ItemListener {

    @Override
    public void onCreated(Item item){
        CurrentStage.CREATED.handleEvent(item, "item");
    }

    @Override
    public void onUpdated(Item item){
        CurrentStage.UPDATED.handleEvent(item, "item");
    }
}
