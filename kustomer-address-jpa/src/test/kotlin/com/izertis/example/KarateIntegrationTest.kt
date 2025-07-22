package com.izertis.example

import com.intuit.karate.Runner
import com.intuit.karate.RuntimeHook
import com.intuit.karate.StringUtils
import com.intuit.karate.cli.IdeMain
import com.intuit.karate.core.ScenarioRuntime
import com.intuit.karate.http.HttpRequest
import com.intuit.karate.http.Response
import com.izertis.example.config.DockerComposeInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.stream.Collectors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DockerComposeInitializer.EnableDockerCompose
internal class KarateIntegrationTest {
    private val classpath = "classpath:" + javaClass.getPackageName().replace(".", "/") + "/"

    @LocalServerPort
    private val port = 0

    @Test
    @Disabled
    @Throws(Exception::class)
    fun run() {
        System.setProperty("karate.server.port", port.toString())

        val karateEnv = defaultString(System.getProperty("karate.env"), "local").lowercase(Locale.getDefault())
        val launchCommand = defaultString(System.getProperty("KARATE_OPTIONS"), "-t ~@ignore " + classpath)

        val options = IdeMain.parseIdeCommandLine(launchCommand)

        val results = Runner.path(
            Optional.ofNullable<MutableList<String?>?>(options.getPaths()).orElse(Arrays.asList<String?>(classpath))
        )
            .hook(coverageRuntimeHook)
            .tags(options.getTags())
            .configDir(options.getConfigDir())
            .karateEnv(karateEnv)
            .outputHtmlReport(true)
            .outputCucumberJson(true)
            .outputJunitXml(true)
            .parallel(options.getThreads())

        moveJUnitReports(results.getReportDir(), "target/surefire-reports")

        // here you can analyze/process coverage
        println("SUCCESS ENDPOINTS")
        println(StringUtils.join(httpCalls, "\n"))
        println("FAILED ENDPOINTS")
        println(StringUtils.join(failedHttpCalls, "\n"))

        Assertions.assertEquals(0, results.getFailCount())
    }

    var httpCalls: MutableList<String?> = ArrayList<String?>()
    var failedHttpCalls: MutableList<String?> = ArrayList<String?>()
    private val coverageRuntimeHook: RuntimeHook = object : RuntimeHook {
        var scenarioHttpCalls: MutableList<String?>? = null

        override fun beforeScenario(sr: ScenarioRuntime?): Boolean {
            scenarioHttpCalls = ArrayList<String?>()
            return true
        }

        override fun afterHttpCall(request: HttpRequest, response: Response, sr: ScenarioRuntime?) {
            scenarioHttpCalls!!.add(
                String.format(
                    "%s %s %s",
                    request.getMethod(),
                    request.getUrl(),
                    response.getStatus()
                )
            )
        }

        override fun afterScenario(sr: ScenarioRuntime) {
            (if (sr.isFailed()) failedHttpCalls else httpCalls).addAll(scenarioHttpCalls!!)
        }
    }

    private fun defaultString(value: String?, defaultValue: String): String {
        return if (value == null) defaultValue else value
    }

    companion object {
        @Throws(IOException::class)
        fun moveJUnitReports(karateReportDir: String, surefireReportDir: String) {
            File(surefireReportDir).mkdirs()
            val xmlFiles: MutableCollection<File?> = Files.find(
                Paths.get(karateReportDir), Int.Companion.MAX_VALUE,
                BiPredicate { filePath: Path?, fileAttr: BasicFileAttributes? ->
                    fileAttr!!.isRegularFile() && filePath.toString().endsWith(".xml")
                })
                .map<File?> { p: Path? -> p!!.toFile() }.collect(Collectors.toList())

            xmlFiles.forEach(Consumer { x: File? ->
                try {
                    Files.copy(
                        x!!.toPath(),
                        Paths.get(surefireReportDir, "/TEST-" + x.getName()),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                } catch (var3: IOException) {
                    var3.printStackTrace()
                }
            })
        }
    }
}
