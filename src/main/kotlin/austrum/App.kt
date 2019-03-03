package austrum

import org.glassfish.hk2.api.Injectee
import org.glassfish.hk2.api.JustInTimeInjectionResolver
import org.glassfish.hk2.api.ServiceLocator
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
import org.jvnet.hk2.annotations.Service
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import kotlin.concurrent.thread
import org.glassfish.hk2.utilities.ServiceLocatorUtilities
import org.jvnet.hk2.annotations.Contract


fun main(args: Array<String>)
{
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")

    val logger = LoggerFactory.getLogger("austrum.AppKt")

    thread(start = true, name = "HttpThread") {
        val app = Austrum()
        val uri = URI.create("http://localhost:8081/")

        try {
            val server = GrizzlyHttpServerFactory.createHttpServer(uri, app, false)
            Runtime.getRuntime().addShutdownHook(thread(start = false) { server.shutdownNow() })
            server.start()

            logger.debug("HTTP API enabled.")
            Thread.currentThread().join()
        } catch (e: InterruptedException) {
            logger.error("interrupted", e)
        } catch (e: IOException) {
            logger.error("io", e)
        }
    }
}

@ApplicationPath("/")
class Austrum : ResourceConfig()
{
    init {
        packages("austrum.resource", "austrum.provider")

        //register(SelectableEntityFilteringFeature::class.java)
        //property(SelectableEntityFilteringFeature.QUERY_PARAM_NAME, "select")
        register(MultiPartFeature::class.java)
        register(JacksonFeature::class.java)
        register(LoggingFeature::class.java)
        register(RolesAllowedDynamicFeature::class.java)

        register(object : AbstractBinder() {
            override fun configure() {
                bind(JITServiceResolver::class.java).to(JustInTimeInjectionResolver::class.java)
            }
        })
    }
}

@Service
class JITServiceResolver : JustInTimeInjectionResolver
{
    @Inject
    lateinit var serviceLocator: ServiceLocator

    override fun justInTimeResolution(injectee: Injectee): Boolean {
        val requiredType = injectee.requiredType

        if (injectee.requiredQualifiers.isEmpty() && requiredType is Class<*>) {
            if (requiredType.name.startsWith("austrum")) {
                val descriptors = ServiceLocatorUtilities.addClasses(serviceLocator, requiredType)
                if (!descriptors.isEmpty()) {
                    return true
                }
            }
        }
        return false
    }
}

@Contract
interface IHelloMaker {
    fun makeHello(): String
}

@Service
class HelloMakerImpl : IHelloMaker {
    override fun makeHello(): String {
        return "Bonjour"
    }
}

class AustrumError(message: String = "",
                   val code: String = "error",
                   status: Int = 400) : ClientErrorException(message, status)
{
    companion object {
        fun notFound(message: String = "404 Not Found") =
            AustrumError(message, "not_found", 404)

        fun forbidden(message: String = "403 Forbidden") =
            AustrumError(message, "forbidden", 403)
    }
}

/*
 * Envelope:
 * {
 *   "ok": true | false,
 *   "error": nullable {
 *     "code": "error_code",
 *     "message": "Error message"
 *   },
 *   "data": T?,
 *   "meta": Any?
 * }
 */
data class Envelope<out T>(val data: T?,
                           val ok: Boolean = true,
                           val error: ErrorData? = null,
                           val meta: Any? = null)
{
    companion object {
        fun err(message: String, code: String = "error") =
            Envelope(null, false, ErrorData(message, code))
    }
}

data class ErrorData(val message: String, val code: String = "error")

