package com.jgg0328.demo.controller

import com.jgg0328.demo.dto.AuthResponse
import com.jgg0328.demo.dto.ProfileResponse
import com.jgg0328.demo.dto.SignInRequest
import com.jgg0328.demo.dto.SignUpRequest
import com.jgg0328.demo.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AuthController(
    val authService: AuthService
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.signUp(request)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody request: SignInRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.signIn(request)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/profile/{email}")
    fun profile(@RequestParam email: String): ResponseEntity<ProfileResponse> {
        return try {
            val response = authService.profile(email)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

}