package com.izertis.example.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import java.util.stream.Stream

class DockerComposeInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

  private val log: Logger = LoggerFactory.getLogger(javaClass)

  /** Use this annotation to activate TestContainers in your test. */
  @Target(AnnotationTarget.CLASS)
  @Retention(AnnotationRetention.RUNTIME)
  @ContextConfiguration(initializers = [DockerComposeInitializer::class])
  annotation class EnableDockerCompose

  private data class Service(
      val name: String,
      val port: Int,
      val envVar: String,
      val envValueTemplate: String?
  )

  companion object {
    private const val DOCKER_COMPOSE_FILE = "./docker-compose.yml"
    private val SERVICES =
        listOf(
            Service("postgresql", 5432, "DATASOURCE_URL", "jdbc:postgresql://%s:%s/app"),
            Service("kafka", 9092, "KAFKA_BOOTSTRAP_SERVERS", "%s:%s"))

    val HOST: String = DockerClientFactory.instance().dockerHostIpAddress()
    val container: DockerComposeContainer<*> =
        DockerComposeContainer(File(DOCKER_COMPOSE_FILE)).withEnv("HOST", HOST)

    init {
      for (service in SERVICES) {
        if ("schema-registry" == service.name) {
          container.withExposedService(
              service.name, service.port, Wait.forHttp("/subjects").forStatusCode(200))
        } else {
          container.withExposedService(service.name, service.port, Wait.forListeningPort())
        }
      }
    }

    var isContainerRunning = false
  }

  override fun initialize(ctx: ConfigurableApplicationContext) {
    if (isDockerComposeRunningAllServices(SERVICES)) {
      log.info(
          "Docker Compose Containers are running from local docker-compose. Skipping TestContainers...")
    } else {
      log.info(
          "Docker Compose Containers are not running from local docker-compose. Starting from TestContainers...")
      if (isContainerRunning) {
        log.info("Docker Compose Containers are already running from TestContainers. Skipping...")
      } else {
        log.info("Starting Docker Compose Containers from TestContainers...")
        container.start()
        isContainerRunning = true

        for (service in SERVICES) {
          val port = container.getServicePort(service.name, service.port)
          log.info("DockerCompose exposed port for {}: {}", service.name, "$HOST:$port")
          log.info("DockerCompose Service {} listening: {}", service.name, isPortOpen(HOST, port))
          if (service.envValueTemplate != null) {
            TestPropertyValues.of(
                    "${service.envVar}=${String.format(service.envValueTemplate, HOST, port)}")
                .applyTo(ctx.environment)
          }
        }
      }
    }
  }

  private fun isDockerComposeRunningAllServices(services: List<Service>): Boolean {
    val serviceNames = services.map { it.name }
    return Stream.of("docker-compose", "docker-compose.exe").anyMatch { cmd ->
      try {
        getDockerComposeRunningServices(cmd, "-f", DOCKER_COMPOSE_FILE, "ps", "--services")
            .containsAll(serviceNames)
      } catch (e: IOException) {
        false
      } catch (e: InterruptedException) {
        false
      }
    }
  }

  @Throws(IOException::class, InterruptedException::class)
  fun getDockerComposeRunningServices(vararg command: String): List<String> {
    val services = ArrayList<String>()
    val process = ProcessBuilder(*command).start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))

    var line: String?
    while (reader.readLine().also { line = it } != null) {
      if (!line!!.isEmpty()) {
        services.add(line!!)
      }
    }

    process.waitFor()
    return services
  }

  fun isPortOpen(host: String, port: Int): Boolean {
    return try {
      Socket(host, port).use { true }
    } catch (e: IOException) {
      false
    }
  }
}
