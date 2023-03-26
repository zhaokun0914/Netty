package org.globebill.nio;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Demo {
    public static void main(String[] args) throws InterruptedException {
        DelayQueue<String> delayQueue = new DelayQueue<>();
        delayQueue.add("Hello, World!", 5, TimeUnit.SECONDS, () -> System.out.println("Delayed action executed at " + new Date()));
        delayQueue.start();
        Thread.sleep(10000);
        System.exit(0);
    }
}
