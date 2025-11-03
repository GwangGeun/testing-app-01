package com.jgg0328.demo.service

import com.jgg0328.demo.db.concurrentMemberHashMap
import com.jgg0328.demo.dto.SignInRequest
import com.jgg0328.demo.dto.SignUpRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthServiceTest {

    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = AuthService()
        concurrentMemberHashMap.clear()
    }

    @AfterEach
    fun tearDown() {
        concurrentMemberHashMap.clear()
    }

    @Test
    fun `signUp should create new member successfully`() {
        // Given
        val request = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )

        // When
        val response = authService.signUp(request)

        // Then
        assertNotNull(response)
        assertEquals("test@example.com", response.email)
        assertEquals("Test User", response.name)
        assertNotNull(response.id)
        assertEquals(1, concurrentMemberHashMap.size)
    }

    @Test
    fun `signUp should throw exception when email already exists`() {
        // Given
        val request = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )
        authService.signUp(request)

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            authService.signUp(request)
        }
        assertEquals("Email already exists", exception.message)
    }

    @Test
    fun `signIn should return user info with valid credentials`() {
        // Given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )
        authService.signUp(signUpRequest)

        val signInRequest = SignInRequest(
            email = "test@example.com",
            password = "password123"
        )

        // When
        val response = authService.signIn(signInRequest)

        // Then
        assertNotNull(response)
        assertEquals("test@example.com", response.email)
        assertEquals("Test User", response.name)
    }

    @Test
    fun `signIn should throw exception with invalid email`() {
        // Given
        val signInRequest = SignInRequest(
            email = "nonexistent@example.com",
            password = "password123"
        )

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            authService.signIn(signInRequest)
        }
        assertEquals("Invalid email or password", exception.message)
    }

    @Test
    fun `signIn should throw exception with invalid password`() {
        // Given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )
        authService.signUp(signUpRequest)

        val signInRequest = SignInRequest(
            email = "test@example.com",
            password = "wrongpassword"
        )

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            authService.signIn(signInRequest)
        }
        assertEquals("Invalid email or password", exception.message)
    }

    @Test
    fun `profile should return user profile with valid email`() {
        // Given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )
        authService.signUp(signUpRequest)

        // When
        val response = authService.profile("test@example.com")

        // Then
        assertNotNull(response)
        assertEquals("test@example.com", response.email)
        assertEquals("Test User", response.name)
    }

    @Test
    fun `profile should throw exception with invalid email`() {
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            authService.profile("nonexistent@example.com")
        }
        assertEquals("Member not found", exception.message)
    }

    @Test
    fun `multiple users should be able to sign up`() {
        // Given
        val request1 = SignUpRequest(
            email = "user1@example.com",
            name = "User One",
            password = "password1"
        )
        val request2 = SignUpRequest(
            email = "user2@example.com",
            name = "User Two",
            password = "password2"
        )

        // When
        val response1 = authService.signUp(request1)
        val response2 = authService.signUp(request2)

        // Then
        assertEquals(2, concurrentMemberHashMap.size)
        assertEquals("user1@example.com", response1.email)
        assertEquals("user2@example.com", response2.email)
    }
}