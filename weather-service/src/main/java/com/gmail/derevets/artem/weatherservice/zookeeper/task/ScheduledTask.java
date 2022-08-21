package com.gmail.derevets.artem.weatherservice.zookeeper.task;


import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.Date;

public class ScheduledTask extends Task {

    public String executionTime;

    public ScheduledTask(String data, String path, Date executionTime) {
        super(data, path);
        this.executionTime = String.valueOf(executionTime.getTime());
    }

    public ScheduledTask(PathChildrenCacheEvent event, String data, Date executionTime) {
        super(event, data);
        this.executionTime = String.valueOf(executionTime.getTime());
    }


}