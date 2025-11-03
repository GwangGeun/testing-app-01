package com.jgg0328.demo.db

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

val concurrentBookingHashMap = ConcurrentHashMap<String, BookingEntity>()

data class BookingEntity(
    val id: String,
    val member: MemberEntity,
    val checkIn: LocalDateTime,
    val checkOut: LocalDateTime
)