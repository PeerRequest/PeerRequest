package com.peerrequest.app;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Do something but only once.
 */
public class Once {
    protected final AtomicBoolean done = new AtomicBoolean();

    protected final Runnable task;

    /**
     * Construct a new Once.
     *
     * @param task task to run once
     */
    public Once(Runnable task) {
        this.task = task;
    }

    /**
     * Execute the task.
     */
    public void run() {
        if (done.get()) {
            return;
        }
        if (done.compareAndSet(false, true)) {
            task.run();
        }
    }
}