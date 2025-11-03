package com.jgg0328.demo.dto

import java.time.LocalDateTime

data class CreateBookingRequest(
    val email: String,
    val checkIn: LocalDateTime,
    val checkOut: LocalDateTime
)

data class AmendBookingRequest(
    val checkIn: LocalDateTime,
    val checkOut: LocalDateTime
)

data class BookingResponse(
    val id: String,
    val memberEmail: String,
    val memberName: String,
    val checkIn: LocalDateTime,
    val checkOut: LocalDateTime
)

data class BookingListResponse(
    val bookings: List<BookingResponse>
)
