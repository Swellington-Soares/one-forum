package br.one.forum.infra.worker.processimage;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ProcessImageQueue {

    private final BlockingQueue<Runnable> queue =  new LinkedBlockingQueue<>();

    public void AddJob(Runnable job) {
        queue.add(job);
   }

    public Runnable takeJob() throws InterruptedException {
        return queue.take();
    }
}
