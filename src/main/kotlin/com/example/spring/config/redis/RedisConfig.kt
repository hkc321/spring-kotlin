package com.example.spring.config.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig(
    @Value("\${spring.data.redis.host}") val host: String,
    @Value("\${spring.data.redis.port}") val port: Int,
    @Value("\${spring.data.redis.password}") val password: String,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration(host, port)
        redisConfig.password = RedisPassword.of(password)

        return LettuceConnectionFactory(redisConfig)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<*, *> {
        return RedisTemplate<Any, Any>().apply {
            this.connectionFactory = redisConnectionFactory()

            // 콘솔 출력 시 "\xac\xed\x00" 같은 불필요한 해시값을 보지 않기 위해 serializer 설정
            this.keySerializer = StringRedisSerializer()
            this.hashKeySerializer = StringRedisSerializer()
            this.valueSerializer = StringRedisSerializer()
        }
    }

//    @Bean
//    fun cacheManager(): RedisCacheManager {
//        val objectMapper = ObjectMapper()
//            .registerModule(JavaTimeModule())
//            .activateDefaultTyping(
//                BasicPolymorphicTypeValidator.builder()
//                    .allowIfBaseType(Any::class.java).build(), ObjectMapper.DefaultTyping.EVERYTHING
//            )
//
//        val redisCacheConfiguration: RedisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
//            .serializeValuesWith(
//                RedisSerializationContext.SerializationPair
//                    .fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper))
//            )
//            .entryTtl(Duration.ofMinutes(3L))
//
//        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory())
//            .cacheDefaults(redisCacheConfiguration).build()
//    }


    fun redisCacheManager(): RedisCacheManager? {
        val redisCacheConfiguration = RedisCacheConfiguration
            .defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper))
            )
            .entryTtl(Duration.ofMinutes(3L))

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(redisCacheConfiguration)
            .build()
    }


    class CustomJsonRedisSerializer<T>(private val objectMapper: ObjectMapper) : RedisSerializer<T> {
        private val delegate: GenericJackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer()
        private val mapper: ObjectMapper = objectMapper

        override fun serialize(value: T?): ByteArray? {
            return delegate.serialize(value)
        }

        override fun deserialize(bytes: ByteArray?): T? {
            return delegate.deserialize(bytes) as T?
        }
    }
}