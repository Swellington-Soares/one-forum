package br.one.forum.component;

import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EMailWorker implements SmartLifecycle {

    private final EmailQueue emailQueue;
    private volatile boolean running = false;

    @Async("taskExecutor")
    public void start() {
        running = true;
        System.out.println("E-mail worker is starting");
        while (!Thread.currentThread().isInterrupted() && running) {
            Runnable job = emailQueue.take(); // bloqueante
            job.run();
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
