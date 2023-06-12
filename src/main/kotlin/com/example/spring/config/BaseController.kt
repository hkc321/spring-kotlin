package com.example.spring.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class BaseController {
    protected val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    protected fun test() = "test"
}