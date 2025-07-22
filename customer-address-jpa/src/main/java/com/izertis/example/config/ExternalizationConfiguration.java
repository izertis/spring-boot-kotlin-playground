package com.izertis.example.config;

import io.zenwave360.modulith.events.scs.config.EnableSpringCloudStreamEventExternalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableSpringCloudStreamEventExternalization
@EnableScheduling
class ExternalizationConfiguration {

    private final Logger log = LoggerFactory.getLogger(ExternalizationConfiguration.class);

    final IncompleteEventPublications incompleteEventPublications;
    private final Map<UUID, AtomicInteger> failedEvents = new HashMap<>();

    public ExternalizationConfiguration(IncompleteEventPublications incompleteEventPublications) {
        this.incompleteEventPublications = incompleteEventPublications;
    }

    @Scheduled(fixedDelay = 1000)
    // Use @SchedulerLock to prevent multiple instances from executing this method at the same time
    public void retryFailedEvents() {
        incompleteEventPublications.resubmitIncompletePublications((ep) -> {
            // Wait 5 seconds before retrying
            if (ep.getPublicationDate().plusSeconds(5).isAfter(java.time.Instant.now())) {
                return false;
            }
            int count = failedEvents.computeIfAbsent(ep.getIdentifier(), k -> new AtomicInteger(0)).incrementAndGet();
            if (count == 3) {
                log.error("Failed to publish event [{}] after {} attempts: {}", ep.getIdentifier(), count, ep.getEvent());
                return false;
            }
            else if (count > 3) {
                return false;
            }
            log.info("Retrying event publication [{}]: {}", ep.getIdentifier(), ((TargetEventPublication) ep).getTargetIdentifier());
            return true;
        });
    }
}
