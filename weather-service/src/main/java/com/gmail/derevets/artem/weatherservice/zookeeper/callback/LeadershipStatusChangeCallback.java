package com.gmail.derevets.artem.weatherservice.zookeeper.callback;


public interface LeadershipStatusChangeCallback {
    /**
     * Will be called on leadership status change
     *
     * @param isLeader true if the status is now changed to become a leader
     */
    void execute(boolean isLeader);
}