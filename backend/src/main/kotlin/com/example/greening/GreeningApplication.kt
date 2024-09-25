package com.example.greening

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class GreeningApplication

fun main(args: Array<String>) {
	runApplication<GreeningApplication>(*args)
}
