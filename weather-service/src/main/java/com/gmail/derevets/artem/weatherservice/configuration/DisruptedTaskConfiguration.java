package com.gmail.derevets.artem.weatherservice.configuration;

import com.gmail.derevets.artem.weatherservice.scheduler.RssAlertFeedProcessService;
import com.gmail.derevets.artem.weatherservice.service.RssFeedProcessService;
import com.gmail.derevets.artem.weatherservice.zookeeper.TasksSyncManager;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.ReactiveTaskAssignmentCallback;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.ScheduledTask;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.algo.RoundRobinDistributionAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DisruptedTaskConfiguration {

    private final RssFeedProcessService rssFeedProcessService;

    @Bean
    public String applicationInstanceId(@Value("${spring.application.name}") String applicationName) {
        return applicationName + UUID.randomUUID();
    }

    @SneakyThrows
    @Bean
    public TasksSyncManager tasksSyncManager(String applicationInstanceId, List<RssAlertFeedProcessService> rssAlertFeedProcessServices) {
        var tasksSyncManager = new TasksSyncManager();
        var client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryForever(3000));
        client.start();
        tasksSyncManager.init(client, applicationInstanceId);
        for (RssAlertFeedProcessService rssAlertFeedProcessService : rssAlertFeedProcessServices) {
            var scope = "/" + rssAlertFeedProcessService.getCountry();
            var callback = DisruptedTaskConfiguration.callback(tasksSyncManager,
                    applicationInstanceId,
                    rssAlertFeedProcessService,
                    rssFeedProcessService);
            tasksSyncManager.register(scope, true, new RoundRobinDistributionAlgorithm(), callback);
        }
        return tasksSyncManager;
    }

    public static ReactiveTaskAssignmentCallback callback(TasksSyncManager tasksSyncManager,
                                                          String applicationInstanceId,
                                                          RssAlertFeedProcessService rssAlertFeedProcessService,
                                                          RssFeedProcessService rssFeedProcessService) {
        return task -> Mono.just(rssAlertFeedProcessService)
                .flatMap(rssFeedProcessService::process)
                .thenReturn(new ScheduledTask(applicationInstanceId, "/", Date.from(Instant.now().plus(5, ChronoUnit.MINUTES))))
                .map(taskToAdd -> tasksSyncManager.deleteAndCreateScheduledTaskSync("/" + rssAlertFeedProcessService.getCountry(), task, taskToAdd))
                .filter(StringUtils::isNotEmpty)
                .onErrorContinue((throwable, o) -> log.error("Exception", throwable))
                .then();
    }
}
