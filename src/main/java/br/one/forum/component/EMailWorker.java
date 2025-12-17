package br.one.forum.component;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EMailWorker {

    private final EmailQueue emailQueue;

    @Async("emailExecutor")
    public void start() {
        while (true) {
            try {
                Runnable job = emailQueue.take();
                job.run();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
