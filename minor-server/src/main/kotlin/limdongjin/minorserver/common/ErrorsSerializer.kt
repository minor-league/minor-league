package limdongjin.minorserver.common

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.boot.jackson.JsonComponent
import org.springframework.validation.Errors

@JsonComponent
class ErrorsSerializer: JsonSerializer<Errors>() {
    override fun serialize(errors: Errors, gen: JsonGenerator, serializers: SerializerProvider) {
        if(!errors.hasErrors()) return

        gen.writeStartObject()
        gen.writeFieldName("errors")
        gen.writeStartArray()
        errors.fieldErrors.forEach { e ->
            gen.writeStartObject()
            gen.writeStringField("field", e.field)
            gen.writeStringField("objectName", e.objectName)
            gen.writeStringField("code", e.code)
            gen.writeStringField("defaultMessage", e.defaultMessage)

            gen.writeStringField("rejectedValue",
                e.rejectedValue?.toString() ?: "null")
            gen.writeEndObject()
        }
        errors.globalErrors.forEach { e ->
            gen.writeStartObject()
            gen.writeStringField("objectName", e.objectName)
            gen.writeStringField("code", e.code)
            gen.writeStringField("defaultMessage", e.defaultMessage)
            gen.writeEndObject()
        }
        gen.writeEndArray()
        gen.writeEndObject()
    }
}