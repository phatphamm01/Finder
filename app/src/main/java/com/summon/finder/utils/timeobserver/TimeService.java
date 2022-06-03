package com.summon.finder.utils.timeobserver;

import java.util.ArrayList;
import java.util.List;

public class TimeService {
    private final List<TimeListener> users;

    public TimeService() {
        users = new ArrayList<>();
    }

    public void subscribe(TimeListener listener) {
        users.add(listener);
    }

    public void unsubscribe(String id) {
        users.removeIf(user -> user.id.equals(id));
    }


    public void notifyCustomers(String newTime) {
        users.forEach(user -> {
            user.update(newTime);
        });
    }
}
