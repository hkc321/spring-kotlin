package com.example.spring.config.slack

import com.slack.api.model.Attachment
import com.slack.api.model.Field
import com.slack.api.webhook.Payload
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SlackApiConfiguration {
    @Bean
    fun slackOkHttp3ConnectionPool() = ConnectionPool()

    @Bean
    fun slackOkHttpClient() = OkHttpClient()

    @Bean
    fun slackPayloadBuilder(): Payload.PayloadBuilder = Payload.builder()

    @Bean
    fun slackAttachmentBuilder(): Attachment.AttachmentBuilder = Attachment.builder()

    @Bean
    fun slackFieldBuilder(): Field.FieldBuilder =  Field.builder()
}