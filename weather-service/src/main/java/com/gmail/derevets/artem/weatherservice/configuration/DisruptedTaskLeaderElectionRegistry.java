package com.gmail.derevets.artem.weatherservice.configuration;

import com.gmail.derevets.artem.weatherservice.scheduler.RssAlertFeedProcessService;
import com.gmail.derevets.artem.weatherservice.zookeeper.TasksSyncManager;
import com.gmail.derevets.artem.weatherservice.zookeeper.task.ScheduledTask;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisruptedTaskLeaderElectionRegistry {
    private final TasksSyncManager tasksSyncManager;
    private final List<RssAlertFeedProcessService> rssAlertFeedProcessServices;

    @EventListener(classes = ApplicationReadyEvent.class)
    public void init() {
        for (RssAlertFeedProcessService rssAlertFeedProcessService : rssAlertFeedProcessServices) {
            var scope = "/" + rssAlertFeedProcessService.getCountry();
            var task = new ScheduledTask("", "", Date.from(Instant.now().plusSeconds(5)));
            tasksSyncManager.createUniqueScheduledTask(scope, task, Long.valueOf(task.executionTime));
        }
    }

    @EventListener(classes = ContextStoppedEvent.class)
    @SneakyThrows
    public void close() {
        tasksSyncManager.close();
    }
}