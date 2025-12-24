package br.one.forum.infra.worker.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@Slf4j
public class ProcessEmailWorker implements SmartLifecycle {

    private final ProcessEmailQueue emailQueue;
    private volatile boolean running = false;
    private final Executor executor;

    public ProcessEmailWorker(
            ProcessEmailQueue emailQueue,
            @Qualifier("taskEmailExecutor") Executor executor
    ) {
        this.emailQueue = emailQueue;
        this.executor = executor;
    }

    @Override
    public void start() {
        if (running) return;
        running = true;
        log.info("Started ProcessEmailWorker");
        executor.execute(this::processLoop);
    }

    private void processLoop() {
        while (running) {
            try {
                var job = emailQueue.takeJob();
                job.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
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
