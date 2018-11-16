package de.pauhull.friends.common.util;

import lombok.Getter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendThreadFactory implements ThreadFactory {

    @Getter
    private static AtomicInteger currentThreadID = new AtomicInteger(0);

    private String taskName;

    public FriendThreadFactory(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(taskName + " Task #" + currentThreadID.incrementAndGet());
        return thread;
    }

}

