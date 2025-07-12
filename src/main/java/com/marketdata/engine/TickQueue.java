package com.marketdata.engine;

import com.marketdata.model.Tick;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TickQueue {

    private final BlockingQueue<Tick> queue = new LinkedBlockingQueue<>(100_000);

    public TickQueue() {
        System.out.println("🧠 TickQueue instance created");
    }

    public void enqueue(Tick tick) {
        queue.offer(tick); // non-blocking, returns false if full
    }

    public Tick dequeue() throws InterruptedException {
        return queue.take(); // blocks if empty
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
