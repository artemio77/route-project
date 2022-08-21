package com.gmail.derevets.artem.weatherservice.zookeeper.latch;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * A base implementation of a Curator based <code>LeaderLatchListener</code>
 *
 * @author Doron Lehmann
 */
@Slf4j
public abstract class CuratorBaseLatch implements LeaderLatchListener {

    public static final Gson gson;

    public CuratorFramework client;

    public String id;
    public String basePath;

    public PathChildrenCache masterCache;
    public LeaderLatch leaderLatch;
    public TreeCacheListener globalListener;
    public TreeCache globalScopeCache;

    public InterProcessSemaphoreMutex nodesChangeLock;

    public int nodeChangeLockAcquireTime = Integer.parseInt(System.getProperty("zookeeper.curator.node.change.lock.acquire.time.in.seconds", "60"));
    public int nodeChangeLockSleepTime = Integer.parseInt(System.getProperty("zookeeper.curator.node.change.lock.sleep.time.in.seconds", "1"));
    public int maxNumberOfTriesForLock = Integer.parseInt(System.getProperty("zookeeper.curator.node.change.lock.max.num.of.tries", "5"));

    static {
        gson = new Gson();
    }

    /**
     * Close the master latch
     */
    public abstract void close();

    /**
     * @throws Exception
     */
    public void runForMaster() throws Exception {
        /*
         * monitoring cache
         */
        if (Boolean.parseBoolean(System.getProperty("zookeeper.debug.curatorMasterLatch", Boolean.TRUE.toString()))) {
            initGlobalListener();
            globalScopeCache = new TreeCache(client, basePath);
            globalScopeCache.getListenable().addListener(globalListener);
            globalScopeCache.start();
        }

        /*
         * Start master election
         */
        log.debug("Starting master selection: {}", id);
        leaderLatch.addListener(this);
        leaderLatch.start();
    }

    /**
     * Checks if the current system has the leadership for this scope
     *
     * @return true if it has the leadership, otherwise false
     */
    public boolean hasLeadership() {
        return leaderLatch != null && leaderLatch.hasLeadership();
    }

    /**
     * Tries to acquire the lock in order to either register to the scope or to reassign the scope tasks
     *
     * @return true if the lock is acquired
     * @throws Exception
     */
    public boolean acquireLock() {
        int tries = 0;
        boolean result = false;
        log.debug("Trying to aquired the nodesChangeLock for scope {}", basePath);
        // we try to acquire the lock for a max number of tries
        while (tries < maxNumberOfTriesForLock) {
            boolean locked = false;
            boolean hasException = false;
            try {
                locked = nodesChangeLock.acquire(nodeChangeLockAcquireTime, TimeUnit.SECONDS);
                if (locked) {
                    log.debug("Successfully aquired the nodesChangeLock for scope {}", basePath);
                    result = true;
                    break;
                }
            } catch (Exception e) {
                // This can happen in cases of ZK errors or connection interruptions
                hasException = true;
            }
            if (hasException) {
                log.error("An error related to ZK occurd while trying to fetch the nodesChangeLock for scope {}. Sleeping for {} seconds - try number {} out of {}", basePath, nodeChangeLockSleepTime, tries, maxNumberOfTriesForLock);
            } else {
                //failed to acquire the lock in the given time frame
                log.error("Failed to fetch the nodesChangeLock for scope {} in the given timeframe of {} seconds. Sleeping for {} seconds - try number {} out of {}", basePath, nodeChangeLockAcquireTime, nodeChangeLockSleepTime, tries, maxNumberOfTriesForLock);
            }
            try {
                Thread.sleep(nodeChangeLockSleepTime * 1000L);
                log.error("Slept for {} seconds for scope {}", nodeChangeLockSleepTime, basePath);
            } catch (InterruptedException e) {
                log.error("Failed to sleep for {} seconds", nodeChangeLockSleepTime, e);
            }
            tries++;
        }
        if (!result) {
            log.error("Failed to fetch the nodesChangeLock for scope {} after {} tries", basePath, tries);
        }
        return result;
    }

    /**
     * Releases the lock
     *
     * @throws Exception
     */
    public void releaseLock() {
        log.debug("Releasing the nodesChangeLock for scope {}", basePath);
        try {
            nodesChangeLock.release();
            log.debug("Released the nodesChangeLock for scope {}", basePath);
        } catch (Exception e) {
            log.error("Exception when trying to release the nodesChangeLock for scope {}", basePath, e);
        }
    }


    /*
     * we add one global listener that listen to all event under the scope ( this.basedir ).
     * this listener can help in monitoring this CuratorMasterCache instance.
     */
    public void initGlobalListener() {
        globalListener = (client, event) -> {
            String path = null;
            ChildData childData = event.getData();
            if (childData != null) {
                path = childData.getPath();
            }
            switch (event.getType()) {
                case CONNECTION_LOST:
                    log.debug("event: CONNECTION_LOST");
                    break;
                case CONNECTION_RECONNECTED:
                    log.debug("event: CONNECTION_RECONNECTED");
                    break;
                case CONNECTION_SUSPENDED:
                    log.debug("event: CONNECTION_SUSPENDED");
                    break;
                case INITIALIZED:
                    log.debug("scope cache INITIALIZED for scope  {}", basePath);
                    break;
                case NODE_ADDED:
                    if (path != null && path.contains("/assign")) {
                        log.debug("Task assigned correctly for path: {}", path);
                    } else if (path != null && path.contains("/tasks")) {
                        log.debug("new Task added for path: {}", path);
                    } else if (path != null && path.contains("/workers")) {
                        log.debug("worker added for path: {}", path);
                    } else if (path != null && path.contains("/master")) {
                        log.debug("master added for path: {}", path);
                    } else {
                        log.debug("NODE_ADDED for path {}", path);
                    }
                    break;
                case NODE_REMOVED:
                    if (path != null && path.contains("/tasks")) {
                        log.debug("Task correctly deleted from the /tasks/ node: {}", path);
                    } else if (path != null && path.contains("/assign")) {
                        log.debug("Task correctly deleted from the /assign/ node: {}", path);
                        break;
                    } else if (path != null && path.contains("/workers")) {
                        log.debug("worker removed for path: {}", path);
                    } else if (path != null && path.contains("/master")) {
                        log.debug("master removed for path: {}", path);
                    } else {
                        log.debug("NODE_REMOVED for path {}", path);
                    }
                    break;
                case NODE_UPDATED:
                    log.debug("NODE_UPDATED for path {}", path);
                    break;
                default:
                    break;
            }
        };
    }

}
