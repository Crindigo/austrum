package austrum.provider

import org.slf4j.LoggerFactory
import java.security.Principal
import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.ext.Provider

@Provider
@Priority(Priorities.AUTHENTICATION)
class SecurityRequestFilter : ContainerRequestFilter
{
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(requestContext: ContainerRequestContext?) {
        val secure = requestContext?.uriInfo?.requestUri?.scheme == "https"
        logger.debug("uri = ${requestContext?.uriInfo?.requestUri}")

        val myRole = requestContext?.uriInfo?.queryParameters?.getFirst("role")

        requestContext?.securityContext = object : SecurityContext {
            override fun isUserInRole(role: String?): Boolean {
                return role == myRole
            }

            override fun getAuthenticationScheme(): String {
                return SecurityContext.FORM_AUTH
            }

            override fun getUserPrincipal(): Principal {
                return Principal { "Hello($myRole)" }
            }

            override fun isSecure(): Boolean {
                return secure
            }

        }
    }
}