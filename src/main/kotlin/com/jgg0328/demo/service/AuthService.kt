package com.jgg0328.demo.service

import com.jgg0328.demo.db.MemberEntity
import com.jgg0328.demo.db.concurrentMemberHashMap
import com.jgg0328.demo.dto.AuthResponse
import com.jgg0328.demo.dto.ProfileResponse
import com.jgg0328.demo.dto.SignInRequest
import com.jgg0328.demo.dto.SignUpRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService {

    fun signUp(request: SignUpRequest): AuthResponse {
        if (concurrentMemberHashMap.containsKey(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val member = MemberEntity(
            id = UUID.randomUUID().toString(),
            name = request.name,
            email = request.email,
            password = request.password
        )
        concurrentMemberHashMap[request.email] = member

        return AuthResponse(
            id = member.id,
            email = member.email,
            name = member.name,
        )
    }

    fun signIn(request: SignInRequest): AuthResponse {
        val member = concurrentMemberHashMap[request.email]
            ?: throw IllegalArgumentException("Invalid email or password")

        if (member.password != request.password) {
            throw IllegalArgumentException("Invalid email or password")
        }

        return AuthResponse(
            id = member.id,
            email = member.email,
            name = member.name,
        )
    }

    fun profile(email: String): ProfileResponse {

        val member = concurrentMemberHashMap[email]
            ?: throw IllegalArgumentException("Member not found")

        return ProfileResponse(
            id = member.id,
            email = member.email,
            name = member.name
        )
    }

}