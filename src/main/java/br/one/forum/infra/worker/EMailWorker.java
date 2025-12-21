package br.one.forum.infra.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EMailWorker implements SmartLifecycle {

    private final EmailQueue emailQueue;
    private volatile boolean running = false;


    @Override
    public void start() {
        if (running) return;
        System.out.println("Starting email worker queue process");
        running = true;
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                var job = emailQueue.takeJob();
                job.run();
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
