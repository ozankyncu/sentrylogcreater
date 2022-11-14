package com.kyncu.sentrylogcreater.component;

import com.google.common.util.concurrent.RateLimiter;
import com.kyncu.sentrylogcreater.input.CommandInput;
import io.sentry.Sentry;
import io.sentry.protocol.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Component
public class SentryOperationsComponent {

    @Value("${application.sentry.dsn:#{null}}")
    private String sentryDsn;

    public void init() {
        Sentry.init(options -> {
                    // Sentry can read the DSN from the environment variable "SENTRY_DSN", the Java System Property "sentry.dsn",
                    // or the "sentry.properties" file in your classpath.
                    options.setEnableExternalConfiguration(true);
                    // java -Dsentry.dsn=https://examplePublicKey@o0.ingest.sentry.io/0 -jar app.jar
                    if (StringUtils.hasLength(sentryDsn)) {
                        options.setDsn(sentryDsn);
                    }
                }
        );
    }

    public void createLog(CommandInput commandInput, RateLimiter limiter, ExecutorService exec, int user) {
        List<Future<?>> tasks = new ArrayList<>(commandInput.getTotalCount());

        for (int i = 0; i < commandInput.getTotalCount(); ++i) {
            tasks.add(exec.submit(() -> {
                for (int j = 0; j < 5; ++j) {
                    limiter.acquire();
                    try {
                        logWithSentryAPI(user);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            }));
        }

        tasks.forEach((future -> {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                log.error(e.getMessage());
            }
        }));
    }

    private void logWithSentryAPI(int userInfo) throws InterruptedException {
        User user = new User();
        user.setEmail("hello@sentry.io" + userInfo);
        Sentry.setUser(user);
        Sentry.addBreadcrumb("User made an action");
        Sentry.setExtra("extra", "thing");
        Sentry.setTag("tagName", "tagValue");
        Sentry.captureMessage("This is a test message for performance testing");
        Thread.sleep(500L);
        System.out.println("Issue created");
    }
}
