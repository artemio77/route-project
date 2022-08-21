package com.gmail.derevets.artem.weatherservice.zookeeper.callback;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public interface ZooKeeperChildrenEventCallback {

    /**
     * Will be called on a child event (i.e. task assignment)
     *
     * @param event
     */
    void execute(PathChildrenCacheEvent event) throws Exception;

}