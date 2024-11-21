package com.example.takterrassen_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.example.takterrassen_backend"])
class BookingApplication

fun main(args: Array<String>) {
    runApplication<BookingApplication>(*args)
}