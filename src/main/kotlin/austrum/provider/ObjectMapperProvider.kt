package austrum.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

@Provider
//@Produces(MediaType.APPLICATION_JSON)
class ObjectMapperProvider : ContextResolver<ObjectMapper>
{
    private val objectMapper: ObjectMapper = ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(KotlinModule())

    override fun getContext(type: Class<*>?): ObjectMapper? = objectMapper
}