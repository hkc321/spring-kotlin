package com.example.spring.application.service.slack

import com.slack.api.model.Attachment
import com.slack.api.model.Field
import com.slack.api.webhook.Payload
import io.sentry.Sentry
import jakarta.servlet.http.HttpServletRequest
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SlackService(
    @Value("\${webhook.slack.url}") val slackWebhookUrl: String,
    private val slackOkHttpClient: OkHttpClient,
    private val slackPayloadBuilder: Payload.PayloadBuilder,
    private val slackAttachmentBuilder: Attachment.AttachmentBuilder,
    private val slackFieldBuilder: Field.FieldBuilder
) {
    var log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    fun sendExceptionMessage(request: HttpServletRequest, ex: Exception) {
        val fields = mutableListOf(
            slackFieldBuilder.title("Request URL").value(request.requestURL.toString()).build(),
            slackFieldBuilder.title("Request Method").value(request.method.toString()).build(),
            slackFieldBuilder.title("Request Time").value(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build(),
            slackFieldBuilder.title("Request IP").value(request.remoteAddr).build(),
            slackFieldBuilder.title("Request User-Agent").value(request.getHeader("User-Agent")).build(),
        )

        val attachment = slackAttachmentBuilder
            .titleLink(request.contextPath)
            .title("Exception")
            .text(ex.stackTraceToString())
            .fallback("Error")
            .color("danger")
            .fields(fields)
            .build()

        val payload = slackPayloadBuilder
            .text("Exception message")
            .attachments(listOf(attachment))
            .build()

        val field = JSONObject(payload)
        val body: RequestBody = field.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request: Request = Request.Builder().url(slackWebhookUrl).post(body).build()
        val client = slackOkHttpClient

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log.warn("fail send to slack")
                Sentry.captureException(e)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                log.info("send to slack")
                response.close()
            }
        })
    }
}