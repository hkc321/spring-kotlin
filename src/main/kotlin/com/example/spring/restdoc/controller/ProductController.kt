package com.example.spring.restdoc.controller

import com.example.spring.restdoc.entity.Product
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/products")
class ProductController {
    private var products: List<Product> = listOf(
        Product("1", "Keyboard", 2000),
        Product("2", "Monitor", 3000),
        Product("3", "Mouse", 1000),
    )

    @GetMapping("")
    fun getProducts(): List<Product> {
        return products
    }

    @GetMapping("/{code}")
    fun getProduct(@PathVariable code: String): Product {
        return products.first { p -> p.code.equals(code, true) }
    }
}