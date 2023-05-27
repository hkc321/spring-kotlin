package com.example.spring.restdoc.controller

import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ProductController::class)
@AutoConfigureRestDocs
class ProductControllerTest{
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `GET v1-product 200 ok`() { // 1

        // When
        val result = mockMvc.perform(get("/v1/products/2"))

        // Then
        result.andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(containsString("Monitor")))
    }

    @Test
    fun `GET v1-product 200 ok document`() { // 2
        // When
        val result = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/products/{code}", 2))

        // Then
        result.andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(containsString("Monitor")))
            .andDo(
                document(
                    "product/get-product-by-id",
                    pathParameters(
                        parameterWithName("code").description("Product Unique Identifier")
                    ), responseFields(
                        fieldWithPath("code").description("Product Unique Identifier"),
                        fieldWithPath("name").description("Name of the product"),
                        fieldWithPath("price").description("Product Price")
                    )
                )
            )
    }
}