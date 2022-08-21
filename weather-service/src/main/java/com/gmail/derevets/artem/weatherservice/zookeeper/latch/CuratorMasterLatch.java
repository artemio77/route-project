package com.gmail.derevets.artem.weatherservice.zookeeper.latch;

import com.gmail.derevets.artem.weatherservice.zookeeper.task.DistributionTask;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.algo.RandomTaskDistributionAlgorithm;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.algo.TaskDistributedAlgorithm;
import com.gmail.derevets.artem.weatherservice.zookeeper.util.ZookeeperUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author Doron Lehmann
 */
public class CuratorMasterLatch extends CuratorBaseLatch {

    private static final Logger logger = LoggerFactory.getLogger(CuratorMasterLatch.class);

    private PathChildrenCache workersCache;
    private PathChildrenCache tasksCache;
    private final TaskDistributedAlgorithm taskDistributionAlgorithm;

    /**
     * Creates a new CuratorMasterLatch
     *
     * @param client                    CuratorFramework ZooKeeper client
     * @param id                        the identifier
     * @param path                      the base path, must start with a backslash "/"
     * @param taskDistributionAlgorithm the <code>TaskDistributedAlgorithm</code> for assigning tasks
     * @param runForMaster              determines if the client is a candidate for becoming a master
     * @param workerNodeExtraData
     * @throws IllegalArgumentException
     * @throws Exception
     */
    public CuratorMasterLatch(CuratorFramework client, String id, String path, TaskDistributedAlgorithm taskDistributionAlgorithm, boolean runForMaster, Map<String, Object> workerNodeExtraData) throws IllegalArgumentException, Exception {
        if (client != null && id != null && !id.isEmpty() && path != null && path.startsWith("/") && workerNodeExtraData != null) {
            this.client = client;
            this.id = id;
            this.basePath = path;
            this.client.newNamespaceAwareEnsurePath(this.basePath + "/nodes_change_lock").ensure(client.getZookeeperClient());
            // we need a lock in order to prevent assign and worker nodes change simultaneously
            nodesChangeLock = new InterProcessSemaphoreMutex(client, this.basePath + "/nodes_change_lock");
            if (!acquireLock()) {
                String msg = "Failed to equire lock in order to start the CuratorMasterLatch for scope " + basePath;
                logger.error(msg);
                throw new Exception(msg);
            }
            try {
                if (taskDistributionAlgorithm == null) {
                    // default algorithm
                    taskDistributionAlgorithm = new RandomTaskDistributionAlgorithm();
                }
                this.taskDistributionAlgorithm = taskDistributionAlgorithm;
                logger.debug("Initilizing the taskDistributionAlgorithm");
                this.taskDistributionAlgorithm.init();
                logger.debug("Initilizied the taskDistributionAlgorithm");
                if (runForMaster) {
                    this.leaderLatch = new LeaderLatch(this.client, this.basePath + "/master", id);
                    this.masterCache = new PathChildrenCache(this.client, this.basePath + "/master", true);
                }
                this.client.newNamespaceAwareEnsurePath(this.basePath + "/workers").ensure(this.client.getZookeeperClient());
                this.client.newNamespaceAwareEnsurePath(this.basePath + "/assign").ensure(this.client.getZookeeperClient());
                this.client.newNamespaceAwareEnsurePath(this.basePath + "/tasks").ensure(this.client.getZookeeperClient());
                this.client.newNamespaceAwareEnsurePath(this.basePath + "/scheduledTasks").ensure(this.client.getZookeeperClient());

                if (client.checkExists().forPath(this.basePath + "/assign/" + generateWorkerId(id)) == null) {
                    logger.debug("Creating assign worker node for scope {}, id {} because it does not exists", this.basePath, this.id);
                    client.inTransaction().create().withMode(CreateMode.PERSISTENT).forPath(this.basePath + "/assign/" + generateWorkerId(id), ZookeeperUtils.toBytes(id)).and().commit();
                    logger.debug("Created assign worker node for scope {}, id {}", this.basePath, this.id);

                }
                String workerPath = this.basePath + "/workers/" + generateWorkerId(id);
                if (client.checkExists().forPath(workerPath) == null) {
                    logger.debug("Creating worker node for scope {}, id {} because it does not exists", this.basePath, this.id);
                    client.inTransaction().create().withMode(CreateMode.EPHEMERAL).forPath(workerPath, ZookeeperUtils.toBytes(gson.toJson(new WorkerNodeData(id, workerNodeExtraData)))).and().commit();
                    logger.debug("Created worker node for scope {}, id {}", this.basePath, this.id);

                } else {
                    // ralon - in order to make sure that the current session with ZK is the one that "owns" the ephemeral worker's znode, we delete and recreate the node
                    logger.debug("Recreating worker node for scope {}, id {}", this.basePath, this.id);
                    ZookeeperUtils.recreateNode(this.client, workerPath, CreateMode.EPHEMERAL, gson.toJson(new WorkerNodeData(id, workerNodeExtraData)));
                    logger.debug("Recreated worker node for scope {}, id {}", this.basePath, this.id);

                }
                // it is important to have this listener at the end of all of the above creations
                if (runForMaster) {
                    logger.debug("Running for master - adding listenerm starting the master cache and calling runForMaster");
                    PathChildrenCacheListener masterCacheListener = new PathChildrenCacheListener() {
                        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                            if (event.getType() == PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED) {
                                if (!acquireLock()) {
                                    logger.error("Failed to acquire lock in order to recreate the worker node after reconnection for {}", id);
                                    throw new Exception();
                                }
                                try {
                                    if (client.checkExists().forPath(basePath + "/workers/" + generateWorkerId(id)) == null) {
                                        logger.debug("Recreating worker node for {} after connection is reconnected", id);
                                        client.create().withMode(CreateMode.EPHEMERAL).forPath(basePath + "/workers/" + generateWorkerId(id), ZookeeperUtils.toBytes(gson.toJson(new WorkerNodeData(id, workerNodeExtraData))));
                                    }
                                } catch (Exception e) {
                                    logger.error("Exception while trying to re-create worker node", e);
                                    throw e;
                                } finally {
                                    releaseLock();
                                }
                            }
                        }
                    };
                    masterCache.getListenable().addListener(masterCacheListener);
                    masterCache.start();
                    runForMaster();
                    logger.debug("Done running the Running for master logic");
                }
            } finally {
                // critical to release the lock
                releaseLock();
            }
        } else {
            throw new IllegalArgumentException("One of the passed parames is invalid. Please see documentation for reference.");
        }
    }

    /**
     * Gets the worker ID
     *
     * @param id
     * @return
     */
    public static String generateWorkerId(String id) {
        return "worker-" + id;
    }

    /**
     * Executed when client becomes leader.
     *
     * @throws Exception
     */
    @Override
    public void isLeader() {
        logger.info("I am the leader: {}", id);
        if (!acquireLock()) {
            logger.error("Exception when starting leadership - Failed to equire lock in order to become leader");
        }
        try {
            /*
             * Start workersCache
             */
            workersCache = new PathChildrenCache(client, basePath + "/workers", true);
            workersCache.getListenable().addListener(workersCacheListener);
            workersCache.start(true);

            tasksCache = new PathChildrenCache(client, basePath + "/tasks", true);
            tasksCache.getListenable().addListener(tasksCacheListener);

            (new RecoveredAssignments(client, basePath)).recover((rc, tasks) -> {
                if (rc == RecoveredAssignments.RecoveryCallback.FAILED) {
                    logger.error("Recovery of assigned tasks failed.");
                } else {
                    logger.debug("Assigning recovered tasks...");
                    for (String task : tasks) {
                        try {
                            assignTask(task);
                        } catch (Exception e) {
                            logger.error("Exception while executing the recovery callback for task {}", task, e);
                        }
                    }
                    logger.debug("Done assigning recovered tasks");
                }
                try {
                    tasksCache.start(true);
                } catch (Exception e) {
                    logger.error("Exception when starting the tasks cache", e);
                }
            });
        } catch (Exception e) {
            logger.error("Exception when starting leadership", e);
        } finally {
            releaseLock();
        }
    }

    @Override
    public void notLeader() {
        logger.debug("Lost leadership - I am {} ", id);
        try {
            workersCache.close();
            tasksCache.close();
        } catch (Exception e) {
            logger.debug("Exception occurred during the leadership lost process", e);
        }
    }

    /**
     * Run over the the nodes under the scheduled tasks path ( Child node is a parent directory of scheduled task and it's name is the execution time of the tasks )
     * and checking if the executionTime ( child node name ) have passed. if yes it moves the tasks under the child node to the tasks path and deletes the child
     */
    public void periodicScheduledTaskCheck() {
        // handle task that their execution time > now
        Date now = new Date();
        Long now_mills = now.getTime();
        String scheduledTasksPath = basePath + "/scheduledTasks";
        List<String> scheduledTasksChildren = null;
        try {
            scheduledTasksChildren = client.getChildren().forPath(scheduledTasksPath);
        } catch (Exception e) {
            logger.error("error in scheduled task processing for path {} - exiting task", scheduledTasksPath, e);
            return;
        }
        if (scheduledTasksChildren == null) {
            logger.debug("no scheduled tasks to proccess for path {}", scheduledTasksPath);
            return;
        }
        for (String taskExecutionTime : scheduledTasksChildren) {
            if (now_mills.compareTo(Long.valueOf(taskExecutionTime)) > 0) {
                try {
                    // moving all the task that under this time stamp and deleting the timestamp parent dir
                    moveToTasks(taskExecutionTime);
                    client.delete().inBackground().forPath(basePath + "/scheduledTasks/" + taskExecutionTime);
                } catch (Exception e) {
                    logger.error("exception while trying to proccess scheduled task directory for path {}", scheduledTasksPath + "/" + taskExecutionTime);
                }
            }
        }
    }

    @Override
    public void close() {
        logger.debug("Closing the {} master latch and closing all of its cache nodes", basePath);
        try {
            if (tasksCache != null) {
                tasksCache.close();
            }
            if (workersCache != null) {
                workersCache.close();
            }
            if (masterCache != null) {
                masterCache.close();
            }
            if (globalScopeCache != null) {
                globalScopeCache.close();
            }
            if (leaderLatch != null) {
                leaderLatch.close();
            }
        } catch (Exception e) {
            logger.error("Failed to close cace node", e);
        } finally {
            if (nodesChangeLock.isAcquiredInThisProcess()) {
                releaseLock();
            }
        }
    }

    /**
     * Generate the path for assigning a task to a worker
     *
     * @param targetWorker The worker to assign the task to
     * @param task         The task to assign to the given worker
     * @return The full path for the task-to-worker assignment
     */
    public String generateTaskAssignPath(String targetWorker, String task) {
        return ZookeeperUtils.generatePath(this.basePath, "assign", targetWorker, task);
    }

    /**
     * Checks the path and returns true if it exists, false otherwise.
     *
     * @param path The path to check
     * @return true if path exists in Zookeeper, false otherwise
     * @throws Exception if client methods throw an exception
     */
    private boolean pathExists(String path) throws Exception {
        return (path != null && this.client.checkExists().forPath(path) != null);
    }

    /*
     * move all tasks under the taskExecutionTime node ( which is under the basePath/scheduledTasks node ) to the basePath/tasks path
     * Usually expecting one task in each taskExecutionTime dir ( with high probability, because we are working in milliseconds ) but the code support more than one task
     */
    private void moveToTasks(String taskExecutionTime) throws Exception {
        String scheduledTaskParentPath = basePath + "/scheduledTasks/" + taskExecutionTime;
        List<String> tasksInDir = client.getChildren().forPath(scheduledTaskParentPath);
        if (tasksInDir == null || tasksInDir.isEmpty()) {
            logger.error("No scheduled task in path {}", scheduledTaskParentPath);
            return;
        }
        for (String scheduledTask : tasksInDir) {
            moveNode(scheduledTaskParentPath + "/" + scheduledTask, basePath + "/tasks");
            logger.debug("sceduledTask {} moved to tasks", scheduledTask);
        }
    }

    /*
     * moves node to be under toPath ( i.e. the toPath parameter is the node's new dad )
     */
    private void moveNode(String nodeFullPath, String toPath) throws Exception {
        byte[] data = client.getData().forPath(nodeFullPath);
        String[] pathArr = nodeFullPath.split("/");
        String nodeName = pathArr[pathArr.length - 1];
        String newPath = toPath + "/" + nodeName;
        // we perform the deletion and creation in one transaction
        client.inTransaction().delete().forPath(nodeFullPath).and().create().withMode(CreateMode.PERSISTENT).forPath(newPath, data).and().commit();
    }

    /**
     * Moves a task from one path to another in a transaction
     *
     * @param taskSourcePath the task original path
     * @param taskDespPath   the task destination path
     * @throws Exception
     */
    private void moveTask(String taskSourcePath, String taskDespPath) throws Exception {
        String fullSourceTaskPath = ZookeeperUtils.generatePath(this.basePath, "tasks", taskSourcePath);
        byte[] taskData = client.getData().forPath(fullSourceTaskPath);
        logger.debug("Moving task from {} to {}", fullSourceTaskPath, taskDespPath);
        if (client.checkExists().forPath(taskDespPath) == null && client.checkExists().forPath(fullSourceTaskPath) != null) {
            client.inTransaction().create().withMode(CreateMode.PERSISTENT)
                    .forPath(taskDespPath, taskData).and()
                    .delete()
                    .forPath(fullSourceTaskPath).and().commit();
        }
        logger.debug("Done moving task from {} to {}", fullSourceTaskPath, taskDespPath);
    }

    /**
     * Assigns a task to chosen worker
     *
     * @param task
     * @throws Exception
     */
    private void assignTask(String task) throws Exception {
        logger.debug("Assigning task {}", task);
        List<String> workersListPaths = new LinkedList<>();
        if (workersCache == null) {
            workersListPaths = client.getChildren().forPath(this.basePath + "/workers");
        } else {
            List<ChildData> currentWorkers = workersCache.getCurrentData();
            if (currentWorkers.isEmpty()) {
                workersListPaths = client.getChildren().forPath(this.basePath + "/workers");
            } else {
                for (ChildData workerChildData : workersCache.getCurrentData()) {
                    workersListPaths.add(workerChildData.getPath());
                }
            }
        }
        assignTaskByWorkers(task, workersListPaths);
    }

    /**
     * Tries to assign the task to one of the workers provided in the list.
     * If task fails to be assigned to the first selected worker and the re-assignment option is enabled -
     * will continue trying to assign the task to different workers in the list until successful or until
     * assignment fails on all workers, in which case throws an exception (Task will then need to be assigned manually).
     *
     * @param task             The task to assign to a worker
     * @param workersListPaths The list of workers the task can be assigned to
     * @throws Exception If the task could not be assigned to any of the given workers
     */
    private void assignTaskByWorkers(String task, List<String> workersListPaths) throws Exception {
        if (ZookeeperUtils.isEmpty(task) || workersListPaths == null || workersListPaths.size() == 0) {
            final String errorMessage = "Cannot assign task - illegal value for task and/or workers list";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        boolean reassignTask = false;
        List<String> workersList = new LinkedList<>(workersListPaths);
        String targetWorker = null;

        do {
            try {
                DistributionTask distTask = new DistributionTask(basePath, task, workersList);
                targetWorker = taskDistributionAlgorithm.getTaskTargetWorker(distTask);
                if (!ZookeeperUtils.isEmpty(targetWorker)) {
                    reassignTask = Boolean.parseBoolean(System.getProperty(ZookeeperUtils.TASK_REASSIGNMENT_CONFIG_PROPERTY, Boolean.TRUE.toString()));
                    String taskTargetPath = generateTaskAssignPath(targetWorker, task);
                    moveTask(task, taskTargetPath);
                    reassignTask = false;
                } else {
                    reassignTask = false;
                    final String errorMessage = "The task distribution algorithm returned a null or empty target worker for task " + ZookeeperUtils.generatePath(this.basePath, "tasks", task);
                    logger.error(errorMessage);
                    throw new Exception(errorMessage);
                }
            } catch (Exception e) {
                final String workerFullPath = ZookeeperUtils.generatePath(this.basePath, "workers", targetWorker);
                final String taskFullPath = ZookeeperUtils.generatePath(this.basePath, "tasks", task);
                logger.error("Could not assign task {} to worker {}", taskFullPath, workerFullPath);

                if (!reassignTask) {
                    throw new Exception("CRITICAL: task " + taskFullPath + " failed to be assigned to a worker!");
                }

                if (!workersList.contains(workerFullPath)) {   // This should never happen, but is checked regardless to avoid an infinite loop
                    logger.error("The task's worker list was modified unexpectedly - not assigning the task {} to avoid an infinite loop", taskFullPath);
                    return;
                }
                workersList.remove(workerFullPath);  // On the next iteration, the algorithm will select a new worker to try to assign to
            }
        } while (reassignTask && workersList.size() > 0);

        if (workersList.size() == 0) {   // Task failed to be assigned to all of the workers in the list
            final String criticalErrorMessage = "CRITICAL: task " + ZookeeperUtils.generatePath(this.basePath, "tasks", task) + " failed to be assigned to a worker!";
            logger.error(criticalErrorMessage);
        }
    }

    private final PathChildrenCacheListener workersCacheListener = (client, event) -> {
        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
            final String workerName = event.getData().getPath().replaceFirst(basePath + "/workers/", "");
            logger.debug("A worker node {} was removed to the 'workers' node", workerName);
            try {
                if (!acquireLock()) {
                    logger.error("Failed to acquire lock in order to recover absent worker {} tasks", workerName);
                    throw new Exception();
                }
                RecoveredAssignments.recoverAbsentWorkerTasks(client, basePath, workerName);
            } catch (Exception e) {
                logger.error("Exception while trying to re-assign tasks", e);
            } finally {
                releaseLock();
            }
        } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
            String data = ZookeeperUtils.fromBytes(event.getData().getData());
            WorkerNodeData workerNodeData = gson.fromJson(data, WorkerNodeData.class);
            logger.debug("A new worker node {} was added to the 'workers' node", workerNodeData.id);
            try {
                if (!acquireLock()) {
                    logger.error("Failed to acquire lock in order to recreate the assign node after reconnection for {}", workerNodeData.id);
                    throw new Exception();
                }
                if (client.checkExists().forPath(basePath + "/assign/" + generateWorkerId(workerNodeData.id)) == null) {
                    logger.debug("The new worker node {} doesn't have a node in the 'assign' node - adding it now", workerNodeData.id);
                    client.create().withMode(CreateMode.PERSISTENT).forPath(basePath + "/assign/" + generateWorkerId(workerNodeData.id), ZookeeperUtils.toBytes(workerNodeData.id));
                }
            } catch (Exception e) {
                logger.error("Exception while trying to add a node in the 'assign' node", e);
            } finally {
                releaseLock();
            }
        }
    };

    private final PathChildrenCacheListener tasksCacheListener = (client, event) -> {
        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
            try {
                assignTask(event.getData().getPath().replaceFirst(basePath + "/tasks/", ""));
            } catch (Exception e) {
                logger.error("Exception when assigning task for {}", event.getData().toString(), e);
            }
        }
    };

}