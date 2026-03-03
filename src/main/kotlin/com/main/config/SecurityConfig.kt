package com.main.config

import com.login.CustomUserDetailsService
import com.login.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
open class SecurityConfig(val userDetailsService: CustomUserDetailsService, val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    open fun authenticationManager(http: HttpSecurity, passwordEncoder: PasswordEncoder): AuthenticationManager {
        val auth = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        auth.userDetailsService<UserDetailsService>(userDetailsService).passwordEncoder(passwordEncoder)
        return auth.build()
    }

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/", "/api/postUser", "/api/login", "/api/checkEmail", "/api/rules").permitAll() // Allow access to the login endpoint without authentication
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .httpBasic { it.disable() }
            .logout { it.disable() }

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    open fun corsConfigurationSource(): org.springframework.web.cors.CorsConfigurationSource {
        val source = org.springframework.web.cors.UrlBasedCorsConfigurationSource()
        val config = org.springframework.web.cors.CorsConfiguration()
        config.allowedOrigins = listOf(
            "http://localhost:5173",
            "https://booknook.no",
            "https://www.booknook.no",
            "http://booknook.no",
            "http://www.booknook.no"
        )
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        source.registerCorsConfiguration("/**", config)
        return source
    }
}


//TROR GREIA ER AT JEG MÅ FINNE PÅ ID-FELTET I MODELLEN OG IKKE DEN MONGO-IDEN..... SÅ JEG FINNER IKKE BRUKERE SOME ER REGISTRERT.... MEN DETTE KLARER VI!