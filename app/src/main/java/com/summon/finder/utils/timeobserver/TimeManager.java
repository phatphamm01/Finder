package com.summon.finder.utils.timeobserver;

public class TimeManager {
    private static TimeManager instance;
    private final TimeService timeService;

    public TimeManager() {
        timeService = new TimeService();
    }

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }

        return instance;
    }

    public void change(String newTime) {
        timeService.notifyCustomers(newTime);
    }

    public TimeService getService() {
        return timeService;
    }
}
