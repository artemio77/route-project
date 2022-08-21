package com.gmail.derevets.artem.weatherservice.zookeeper.task.algo;


import com.gmail.derevets.artem.weatherservice.zookeeper.task.DistributionTask;

import java.util.Random;


/**
 * This implementation randomly assign a task to a worker
 *
 * @author Doron Lehmann
 *
 */
public class RandomTaskDistributionAlgorithm implements TaskDistributedAlgorithm {

    private Random rand = new Random( System.currentTimeMillis() );

    @Override
    public String getTaskTargetWorker( DistributionTask task ) {
        int index = rand.nextInt( task.workers.size() );
        String designatedWorker = task.workers.get( index ).replaceFirst( task.basePath + "/workers/", "" );
        return designatedWorker;
    }

    @Override
    public void init() {}

}