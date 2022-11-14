package com.kyncu.sentrylogcreater;

import com.google.common.util.concurrent.RateLimiter;
import com.kyncu.sentrylogcreater.component.CommandOperationsComponent;
import com.kyncu.sentrylogcreater.component.SentryOperationsComponent;
import com.kyncu.sentrylogcreater.input.CommandInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SpringBootApplication
public class SentryLogCreaterApplication implements CommandLineRunner {

    @Autowired
    private SentryOperationsComponent sentryOperationsComponent;

    @Autowired
    private CommandOperationsComponent commandOperationsComponent;


    public static void main(String[] args) {
        SpringApplication.run(SentryLogCreaterApplication.class, args);
    }

    private void createLogs() {
        CommandInput commandInput = prepareLogCreationOperations();
        initializeLogCreation(commandInput);
    }

    private void initializeLogCreation(CommandInput commandInput) {
        long started = System.currentTimeMillis();
        IntStream.range(0, commandInput.getUserCount()).parallel().forEach(
                user -> {
                    ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                    RateLimiter limiter = RateLimiter.create(6.0F);
                    sentryOperationsComponent.createLog(commandInput, limiter, exec, user);
                }
        );
        long ended = System.currentTimeMillis();
        System.out.println("Issue creation completed : " + (ended - started));
    }

    private CommandInput prepareLogCreationOperations() {
        CommandInput commandInput = commandOperationsComponent.getCommandInputs();
        sentryOperationsComponent.init();
        return commandInput;
    }

    @Override
    public void run(String... args) throws Exception {
        createLogs();
    }
}
