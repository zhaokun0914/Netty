package org.globebill.nio;

import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DelayQueue<T> {
    private PriorityQueue<DelayedTask<T>> queue = new PriorityQueue<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void add(T element, long delay, TimeUnit unit, Runnable action) {
        long delayInMillis = TimeUnit.MILLISECONDS.convert(delay, unit);
        DelayedTask<T> task = new DelayedTask<>(element, delayInMillis, action);
        queue.offer(task);
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            while (!queue.isEmpty() && queue.peek().isExpired()) {
                DelayedTask<T> task = queue.poll();
                task.getAction().run();
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    private static class DelayedTask<T> implements Comparable<DelayedTask<T>> {
        private T element;
        private long delayInMillis;
        private Runnable action;
        private long expirationTime;

        public DelayedTask(T element, long delayInMillis, Runnable action) {
            this.element = element;
            this.delayInMillis = delayInMillis;
            this.action = action;
            this.expirationTime = System.currentTimeMillis() + delayInMillis;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expirationTime;
        }

        public Runnable getAction() {
            return action;
        }

        @Override
        public int compareTo(DelayedTask<T> o) {
            return Long.compare(this.expirationTime, o.expirationTime);
        }
    }
}
