package br.one.forum.component;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class EmailQueue {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public void add(Runnable job)  {
        try {
            queue.put(job);
        } catch (InterruptedException _) {}
    }

    public Runnable take()  {
        try {
            return queue.take();
        } catch (InterruptedException _) {
            return null;
        }
    }
}
