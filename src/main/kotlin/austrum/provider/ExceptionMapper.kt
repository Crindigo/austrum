package austrum.provider

import austrum.AustrumError
import austrum.Envelope
import org.slf4j.LoggerFactory
import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@Provider
class ExceptionMapper : javax.ws.rs.ext.ExceptionMapper<Exception>
{
    override fun toResponse(exception: Exception): Response {
        LoggerFactory.getLogger(javaClass).error("", exception)
        val status = when (exception) {
            is ClientErrorException -> exception.response.status
            else -> 500
        }
        val code = when (exception) {
            is AustrumError -> exception.code
            else -> "error"
        }

        return Response.status(status)
            .entity(Envelope.err(exception.message.orEmpty(), code)).build()
    }
}