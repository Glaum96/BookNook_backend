package com.main

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@ComponentScan(basePackages = ["com.users", "com.bookings", "com.main", "com.login", "com.rules"])
open class MainApplication

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}