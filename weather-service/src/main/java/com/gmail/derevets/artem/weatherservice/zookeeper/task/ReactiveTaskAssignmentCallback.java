package com.gmail.derevets.artem.weatherservice.zookeeper.task;


import reactor.core.publisher.Mono;

public interface ReactiveTaskAssignmentCallback {

    /**
     * Will be called on task assignment
     *
     * @param task
     */
    Mono<Void> execute(Task task) throws Exception;


}