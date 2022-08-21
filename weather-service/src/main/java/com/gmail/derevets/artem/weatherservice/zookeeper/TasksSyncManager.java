package com.gmail.derevets.artem.weatherservice.zookeeper;


import com.gmail.derevets.artem.weatherservice.zookeeper.latch.CuratorMasterLatch;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.ReactiveTaskAssignmentCallback;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.ScheduledTask;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.Task;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.algo.TaskDistributedAlgorithm;
import com.gmail.derevets.artem.weatherservice.zookeeper.util.BaseWorkerThread;
import com.gmail.derevets.artem.weatherservice.zookeeper.util.HouseKeeperWorker;
import com.gmail.derevets.artem.weatherservice.zookeeper.util.ZookeeperUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * @author Doron Lehmann
 */

@Getter
public class TasksSyncManager {

    private static final Logger logger = LoggerFactory.getLogger(TasksSyncManager.class);

    public CuratorFramework client;
    private final Map<String, CuratorMasterLatch> masterLatchMap;
    private String id;
    private final HouseKeeperWorker worker;

    /**
     * A thread pool for managing the registration of the master latch
     */
    private final ExecutorService zkExPool;
    private final Map<String, Future<?>> scopeRegistrationMap;


    /**
     * Creates a new Tasks sync manager
     */
    public TasksSyncManager() {
        masterLatchMap = new ConcurrentHashMap<>();
        scopeRegistrationMap = new ConcurrentHashMap<>();
        worker = new HouseKeeperWorker(this);
        zkExPool = Executors.newFixedThreadPool(Integer.parseInt(System.getProperty("task.sync.manager.zk.threadpool.size", "10")));
    }

    /**
     * Initialize the manager with the given CuratorFramework client.<br>
     * Each manager should be closed by calling the close() method once it is no longer in use.
     *
     * @param client CuratorFramework client instance
     * @param id     the id of the application which inits the manager
     * @throws Exception if failed to init the tasks sync manager
     */
    @SneakyThrows
    public void init(CuratorFramework client, String id) {
        logger.info("Initializing the Distributed Sync Manager...");
        Mono.fromRunnable(() -> setAndStart(client, id)).then().toFuture().get();
        logger.info("Initilized the Distributed Sync Manager");
    }

    @SneakyThrows
    private void setAndStart(CuratorFramework curatorFrameworkClient, String id) {
        if (curatorFrameworkClient != null) {
            this.client = curatorFrameworkClient;
            this.id = id;
            this.worker.start();
        } else {
            String message = "Failed to create and start the CuratorFramework client - the CuratorFramework instance is null";
            logger.error(message);
            throw new Exception(message);
        }
    }

    /**
     * Closes all the relevant connections of the manager
     */
    public void close() {
        logger.info("Closing the Distributed Sync Manager...");
        worker.baseStop();
        worker.baseJoin();
        for (CuratorMasterLatch masterLatch : masterLatchMap.values()) {
            masterLatch.close();
        }
        ZookeeperService.closeClient(client);
        zkExPool.shutdown();
        logger.info("Closed the Distributed Sync Manager");

    }

    /**
     * Registers to the provided scope<br>
     * Registered instances will select a master node which will manage the registered instances tasks
     *
     * @param scope                     the scope to register to - must start with a backslash "/"
     * @param runForMaster              true if the manager should run for master role, <br> false for workers scenario which do not want to acquire leadership
     * @param taskDistributionAlgorithm the implementation for distributing the tasks. If null is passed, The random algorithm will be used.
     * @param callback                  the callback that should be executed on task assignment
     * @param workerNodeExtraData       to be set on the registered system worker node
     * @throws Exception
     */
    public void register(final String scope, final boolean runForMaster, final TaskDistributedAlgorithm taskDistributionAlgorithm, final ReactiveTaskAssignmentCallback callback, final Map<String, Object> workerNodeExtraData) throws Exception {
        logger.info("Registering to the Distributed Sync Manager - scope {} ", scope);
        scopeRegistrationMap.put(scope, zkExPool.submit(() -> {
            try {
                logger.info("Calling Create And Start MasterLatch for scope {}", scope);
                CuratorMasterLatch latch = ZookeeperService.createAndStartMasterLatch(client, id, scope, taskDistributionAlgorithm, runForMaster, workerNodeExtraData);
                logger.info("Adding the MasterLatch for scope {} to the masterLatchMap", scope);
                masterLatchMap.put(scope, latch);
                ZookeeperService.createAndAddTasksAssignmentListener(client, id, scope, event -> {
                    Task task = new Task(event, ZookeeperUtils.fromBytes(client.getData().forPath(event.getData().getPath())));
                    logger.debug("Got a new task - {} ", task);
                    callback.execute(task).subscribe();
                });
            } catch (Exception e) {
                logger.error("Failed to register to the Distributed Sync Manager - scope {} ", scope, e);
            }
        }));
    }

    /**
     * Registers to the provided scope<br>
     * Registered instances will select a master node which will manage the registered instances tasks
     *
     * @param scope                     the scope to register to - must start with a backslash "/"
     * @param runForMaster              true if the manager should run for master role, <br> false for workers scenario which do not want to acquire leadership
     * @param taskDistributionAlgorithm the implementation for distributing the tasks. If null is passed, The random algorithm will be used.
     * @param callback                  the callback that should be executed on task assignment
     * @throws Exception
     */

    public void register(final String scope, final boolean runForMaster, final TaskDistributedAlgorithm taskDistributionAlgorithm, final ReactiveTaskAssignmentCallback callback) throws Exception {
        register(scope, runForMaster, taskDistributionAlgorithm, callback, new HashMap<>());
    }

    /**
     * Create a task with execution time. Only guarantee is that the task will be executed after the timeToExecute <br>
     * implemented by working thread that wakes up every 10 seconds and transfer the task to "runnable" state
     *
     * @param scope         - the scope to add the task to - must start with a backslash "/"
     * @param task          - the task to execute
     * @param timeToExecute - milliseconds since the epoch. The task will be executed after this time.
     * @throws Exception
     */

    @SneakyThrows
    public void createUniqueScheduledTask(String scope, ScheduledTask task, Long timeToExecute) {
        logger.debug("Creating scheduled task for {} for scope {}", task, scope);
        CuratorMasterLatch scopeCuratorMasterLatch = getMasterLatch(scope);
        String path = scopeCuratorMasterLatch.basePath + "/scheduledTasks/" + timeToExecute + "/task-";
        final List<String> strings = client.getChildren().forPath(scopeCuratorMasterLatch.basePath + "/scheduledTasks");
        for (String string : strings) {
            client.delete().idempotent().deletingChildrenIfNeeded().forPath(scopeCuratorMasterLatch.basePath + "/scheduledTasks/" + string);
        }
        client.create().idempotent().creatingParentsIfNeeded().withProtection().withMode(CreateMode.PERSISTENT).inBackground().forPath(path, ZookeeperUtils.toBytes(task.data));
    }

    @SneakyThrows
    public String deleteAndCreateScheduledTaskSync(String scope, Task taskToDelete, ScheduledTask taskToAdd) {
        logger.debug("Deleting task: {} and creating task for {}", taskToDelete, taskToAdd);
        CuratorMasterLatch scopeCuratorMasterLatch = getMasterLatch(scope);
        String taskToDeletePath = taskToDelete.path;
        String metadata = taskToAdd.getMetadata() != null ? "-" + taskToAdd.getMetadata() : "";
        String taskToAddPath = scopeCuratorMasterLatch.basePath + "/tasks/task" + metadata + "-";
        //we perform the deletion and creation in one transaction

        if (client.checkExists().forPath(taskToAddPath) != null) {
            client.inTransaction().
                    delete().forPath(taskToAddPath).
                    and().commit();
        }

        if (client.checkExists().forPath(taskToDeletePath) != null) {
            client.inTransaction().
                    delete().forPath(taskToDeletePath).
                    and().commit();
        }

        createUniqueScheduledTask(scope, taskToAdd, Long.valueOf(taskToAdd.executionTime));
        return taskToAddPath;
    }

    /**
     * Gets the CuratorMasterLatch. Each method which needs to use the master latch must call this method in order to get it.
     *
     * @param scope
     * @return the CuratorMasterLatch
     * @throws Exception
     */
    public CuratorMasterLatch getMasterLatch(String scope) throws Exception {
        getMasterLatchFromFuture(scope);
        CuratorMasterLatch scopeCuratorMasterLatch = masterLatchMap.get(scope);
        if (scopeCuratorMasterLatch == null) {
            throw new IllegalArgumentException("No latch for scope " + scope);
        }
        return scopeCuratorMasterLatch;
    }



    private void getMasterLatchFromFuture(String scope) throws Exception {
        Future<?> masterLatchFuture = scopeRegistrationMap.get(scope);
        if (masterLatchFuture == null) {
            throw new IllegalArgumentException("No future latch for scope " + scope);
        }
        if (!masterLatchFuture.isDone()) {
            try {
                masterLatchFuture.get(Long.parseLong(System.getProperty("task.sync.manager.future.get.timeout", "300")), TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("Error while trying to check the registration state for scope {}", scope);
                throw e;
            }
        }
    }

}