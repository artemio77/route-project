package com.gmail.derevets.artem.weatherservice.zookeeper.util;

import com.gmail.derevets.artem.weatherservice.zookeeper.TasksSyncManager;
import com.gmail.derevets.artem.weatherservice.zookeeper.latch.CuratorMasterLatch;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HouseKeeperWorker extends BaseWorkerThread {

    public HouseKeeperWorker(TasksSyncManager tasksSyncManager) {
        super("tasks_sync_manager_house_keeper_worker", 0);
        setTimer(Long.parseLong(System.getProperty("task.sync.manager.worker.time", "10")),
                TimeUnit.SECONDS,
                true,
                null,
                (TimerCallback<Void>) ctx -> {
                    for (String scope : tasksSyncManager.getMasterLatchMap().keySet()) {
                        try {
                            CuratorMasterLatch latch = tasksSyncManager.getMasterLatch(scope);
                            if (latch.hasLeadership()) {
                                latch.periodicScheduledTaskCheck();
                            }
                        } catch (Exception e) {
                            log.error("error while trying to get the master latch");
                        }
                    }
                    for (CuratorMasterLatch latch : tasksSyncManager.getMasterLatchMap().values()) {
                        if (latch.hasLeadership()) {
                            latch.periodicScheduledTaskCheck();
                        }
                    }
                });
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public void loop() {
        while (running) {
            checkPoint("Starting loop");
            if (!suspended) {
                triggerTimers();
            }
            baseSleep(1L, TimeUnit.SECONDS);
        }
    }
}
