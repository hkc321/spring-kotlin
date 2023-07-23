package com.example.spring

import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication


class ApplicationTests {

    @Test
    fun contextLoads() {
        val someArray = arrayOf("test")
        mockkStatic(SpringApplication::class)

        every { SpringApplication.run(any()) } returns null

        main(someArray)

        verify { SpringApplication.run(Application::class.java, *someArray) }

        unmockkStatic(SpringApplication::class)
    }

}
