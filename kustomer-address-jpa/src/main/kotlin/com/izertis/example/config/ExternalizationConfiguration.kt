package com.izertis.example.config

import io.zenwave360.modulith.events.scs.config.EnableSpringCloudStreamEventExternalization
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.modulith.events.EventPublication
import org.springframework.modulith.events.IncompleteEventPublications
import org.springframework.modulith.events.core.TargetEventPublication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate

@Configuration
@EnableSpringCloudStreamEventExternalization
@EnableScheduling
internal open class ExternalizationConfiguration(val incompleteEventPublications: IncompleteEventPublications) {
    private val log: Logger = LoggerFactory.getLogger(ExternalizationConfiguration::class.java)

    private val failedEvents: MutableMap<UUID?, AtomicInteger?> = HashMap<UUID?, AtomicInteger?>()

    @Scheduled(fixedDelay = 1000)
    // Use @SchedulerLock to prevent multiple instances from executing this method at the same time
    fun retryFailedEvents() {
        incompleteEventPublications.resubmitIncompletePublications { ep ->
            // Wait 5 seconds before retrying
            if (ep.publicationDate.plusSeconds(5).isAfter(Instant.now())) {
                return@resubmitIncompletePublications false
            }

            val count = failedEvents.computeIfAbsent(ep.identifier) { AtomicInteger(0) }
                ?.incrementAndGet()

            when {
                count == 3 -> {
                    log.error("Failed to publish event [{}] after {} attempts: {}", ep.identifier, count, ep.event)
                    false
                }
                count!! > 3 -> false
                else -> {
                    log.info("Retrying event publication [{}]: {}", ep.identifier, (ep as TargetEventPublication).targetIdentifier)
                    true
                }
            }
        }
    }
}
