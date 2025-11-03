package com.jgg0328.demo.db

import java.util.concurrent.ConcurrentHashMap

val concurrentMemberHashMap = ConcurrentHashMap<String, MemberEntity>()

data class MemberEntity(
    val id: String,
    val name: String,
    val email: String,
    val password: String
)

