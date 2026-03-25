package semo.backend.controller.request

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

@JsonDeserialize(using = PatchValueDeserializer::class)
sealed class PatchValue<out T> {
    data object Undefined : PatchValue<Nothing>()

    data class Defined<T>(
        val value: T,
    ) : PatchValue<T>()

    companion object {
        fun <T> undefined(): PatchValue<T> = Undefined

        fun <T> of(value: T): PatchValue<T> = Defined(value)
    }
}

class PatchValueDeserializer(
    private val valueType: JavaType? = null,
) : StdDeserializer<PatchValue<*>>(PatchValue::class.java), ContextualDeserializer {
    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext,
    ): PatchValue<*> {
        val targetType = valueType ?: context.constructType(Any::class.java)
        val value = context.readValue<Any?>(parser, targetType)
        return PatchValue.of(value)
    }

    override fun getNullValue(context: DeserializationContext): PatchValue<*> {
        return PatchValue.of(null)
    }

    override fun createContextual(
        context: DeserializationContext,
        property: BeanProperty?,
    ): JsonDeserializer<*> {
        val contextualType = property?.type?.containedType(0) ?: context.contextualType?.containedType(0)
        return PatchValueDeserializer(contextualType)
    }
}
