package com.izertis.example.config

import io.zenwave360.modulith.events.scs.config.EnableSpringCloudStreamEventExternalization
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.modulith.events.EventPublication
import org.springframework.modulith.events.IncompleteEventPublications
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.function.Predicate

@Configuration
@EnableSpringCloudStreamEventExternalization
@EnableScheduling
internal open class ExternalizationConfiguration {
  private val log: Logger = LoggerFactory.getLogger(ExternalizationConfiguration::class.java)

  @Autowired var incompleteEventPublications: IncompleteEventPublications? = null

  @Scheduled(fixedRate = 30000)
  fun resubmitIncompletePublications() {
    incompleteEventPublications!!.resubmitIncompletePublications(
        Predicate { eventPublication: EventPublication? ->
          // Handle resubmitted publication
          log.debug("Resubmitted publication: {}", eventPublication)
          true
        })
  }
}
