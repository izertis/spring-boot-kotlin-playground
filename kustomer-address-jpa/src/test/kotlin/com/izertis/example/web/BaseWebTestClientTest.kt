package com.izertis.example.web

// import com.izertis.examples.config.DockerComposeInitializer
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
// @DockerComposeInitializer.EnableDockerCompose
@org.springframework.transaction.annotation.Transactional
abstract class BaseWebTestClientTest {

    @Autowired
    protected lateinit var context: WebApplicationContext
    protected lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup() {
        this.webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.context).build()
    }
}
