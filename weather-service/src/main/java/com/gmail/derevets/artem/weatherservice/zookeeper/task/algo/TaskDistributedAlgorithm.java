package com.gmail.derevets.artem.weatherservice.zookeeper.task.algo;

import com.gmail.derevets.artem.weatherservice.zookeeper.task.DistributionTask;

/**
 * An interface for implementing task distribution algorithms
 *
 * @author Doron Lehmann
 */
public interface TaskDistributedAlgorithm {

    /**
     * Gets a task target worker name according to the implemented algorithm
     *
     * @param task a DistributionTask object
     * @return the destination worker name
     */
    public String getTaskTargetWorker(DistributionTask task) throws Exception;


    /**
     * Initialize the algorithm instance
     */
    public void init();

}