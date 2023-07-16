package com.example.spring.config.redis

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Configuration
class RedisMapperConfig {


    @Bean
    fun serializingObjectMapper(): ObjectMapper? {
        val ptv: PolymorphicTypeValidator = BasicPolymorphicTypeValidator
            .builder()
            .allowIfSubType(Any::class.java)
            .build()
        val objectMapper = ObjectMapper()
        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
        javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
        /*
        */objectMapper.registerModules(javaTimeModule, Jdk8Module())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//        objectMapper.activateDefaultTypingAsProperty(ptv,ObjectMapper.DefaultTyping.NON_FINAL, "@class")
        objectMapper.activateDefaultTyping(ptv)
        return objectMapper
    }


    class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        @Throws(IOException::class)
        override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString(value.format(formatter))
        }
    }


    class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime?>() {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        @Throws(IOException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDateTime {
            return LocalDateTime.parse(p.valueAsString, formatter)
        }
    }

}