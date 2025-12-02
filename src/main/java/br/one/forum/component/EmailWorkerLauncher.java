package br.one.forum.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EmailWorkerLauncher {

    private final EMailWorker emailWorker;

    @PostConstruct
    void init() {
        emailWorker.start();
    }
}
