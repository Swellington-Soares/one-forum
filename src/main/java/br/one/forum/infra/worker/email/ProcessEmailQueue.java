package br.one.forum.infra.worker.email;


import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class ProcessEmailQueue {

    private final BlockingQueue<Runnable> emailQueue = new LinkedBlockingDeque<>();

    public void addJob(Runnable job) {
        emailQueue.add(job);
    }

    public Runnable takeJob() throws InterruptedException {
        return emailQueue.take();
    }

}
