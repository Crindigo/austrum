package austrum.resource

import austrum.AustrumError
import austrum.Envelope
import austrum.HelloMakerImpl
import org.slf4j.LoggerFactory
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class HomeResource
{
    companion object {
        private val logger = LoggerFactory.getLogger(HomeResource::class.java)
    }

    @Inject
    lateinit var helloMaker: HelloMakerImpl

    @GET
    fun index(@Context security: SecurityContext): Envelope<String> {
        val p = security.userPrincipal?.name
        return Envelope("${helloMaker.makeHello()} world: $p")
    }

    @GET
    @Path("error")
    @RolesAllowed("admin")
    fun errorTest()
    {
        throw AustrumError("test message", "test", 403)
    }
}
