package com.jgg0328.demo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jgg0328.demo.db.concurrentMemberHashMap
import com.jgg0328.demo.dto.SignInRequest
import com.jgg0328.demo.dto.SignUpRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        concurrentMemberHashMap.clear()
    }

    @AfterEach
    fun tearDown() {
        concurrentMemberHashMap.clear()
    }

    @Test
    fun `POST signup should create new user successfully`() {
        // Given
        val request = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `POST signup should return bad request when email already exists`() {
        // Given
        val request = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )

        // First signup
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)

        // When & Then - Second signup with same email
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `POST signin should return user info with valid credentials`() {
        // Given - First create a user
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )

        val signInRequest = SignInRequest(
            email = "test@example.com",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `POST signin should return bad request with invalid email`() {
        // Given
        val signInRequest = SignInRequest(
            email = "nonexistent@example.com",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `POST signin should return bad request with invalid password`() {
        // Given - First create a user
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )

        val signInRequest = SignInRequest(
            email = "test@example.com",
            password = "wrongpassword"
        )

        // When & Then
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET profile should return user profile with valid email`() {
        // Given - First create a user
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            name = "Test User",
            password = "password123"
        )
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )

        // When & Then
        mockMvc.perform(
            get("/profile/test@example.com")
                .param("email", "test@example.com")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `GET profile should return bad request with invalid email`() {
        // When & Then
        mockMvc.perform(
            get("/profile/nonexistent@example.com")
                .param("email", "nonexistent@example.com")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `complete user flow - signup, signin, profile`() {
        // Given
        val signUpRequest = SignUpRequest(
            email = "flow@example.com",
            name = "Flow User",
            password = "securepass"
        )

        // Sign up
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("flow@example.com"))

        // Sign in
        val signInRequest = SignInRequest(
            email = "flow@example.com",
            password = "securepass"
        )
        mockMvc.perform(
            post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("flow@example.com"))

        // Get profile
        mockMvc.perform(
            get("/profile/flow@example.com")
                .param("email", "flow@example.com")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("flow@example.com"))
            .andExpect(jsonPath("$.name").value("Flow User"))
    }
}