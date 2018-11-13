package de.pauhull.friends.util;

import lombok.Getter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendThreadFactory implements ThreadFactory {

    @Getter
    private static AtomicInteger currentThreadID = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("Friends Task #" + currentThreadID.incrementAndGet());
        return thread;
    }

}

