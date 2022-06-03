package com.summon.finder.utils.timeobserver;

abstract class TimeListener {
    String id;

    public TimeListener(String id) {
        this.id = id;
    }

    abstract void update(String newTime);
}

