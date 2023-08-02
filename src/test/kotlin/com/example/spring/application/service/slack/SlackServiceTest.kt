package com.example.spring.application.service.slack

import com.slack.api.model.Attachment
import com.slack.api.model.Field
import com.slack.api.webhook.Payload
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import okhttp3.*

class SlackServiceTest : StringSpec({
    "slack message test" {
        val slackOkHttpClient = OkHttpClient()
        val slackWebhookUrl = "http://www.testasdwrdsserasd.com"

        val slackPayloadBuilder = Payload.builder()
        val slackAttachmentBuilder = Attachment.builder()
        val slackFieldBuilder = Field.builder()

        val httpServeltRequest = mockk<HttpServletRequest>()
        val exception = Exception("Test Exception")
        val stringBuffer = mockk<StringBuffer>()

        every { httpServeltRequest.requestURL } returns stringBuffer
        every { httpServeltRequest.contextPath } returns "testContextPath"
        every { httpServeltRequest.method } returns "POST"
        every { httpServeltRequest.remoteAddr } returns "testAddr"
        every { httpServeltRequest.getHeader("User-Agent") } returns "Postman"

        val slackService = SlackService(slackWebhookUrl, slackOkHttpClient, slackPayloadBuilder, slackAttachmentBuilder, slackFieldBuilder)

        slackService.sendExceptionMessage(httpServeltRequest, exception)
    }
})
