package com.example.config

import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CorsFilter : HttpFilter() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173")
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "*")
        response.setHeader("Access-Control-Allow-Credentials", "true")
        chain.doFilter(request, response)
    }

    override fun init(filterConfig: FilterConfig) {
        // No initialization needed
    }

    override fun destroy() {
        // No cleanup needed
    }
}