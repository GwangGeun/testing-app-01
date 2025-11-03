package com.jgg0328.demo.service

import com.jgg0328.demo.db.BookingEntity
import com.jgg0328.demo.db.concurrentBookingHashMap
import com.jgg0328.demo.db.concurrentMemberHashMap
import com.jgg0328.demo.dto.AmendBookingRequest
import com.jgg0328.demo.dto.BookingListResponse
import com.jgg0328.demo.dto.BookingResponse
import com.jgg0328.demo.dto.CreateBookingRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class BookingService {

    fun createBooking(request: CreateBookingRequest): BookingResponse {
        val member = concurrentMemberHashMap[request.email]
            ?: throw IllegalArgumentException("Member not found")

        if (request.checkIn.isAfter(request.checkOut)) {
            throw IllegalArgumentException("Check-in date must be before check-out date")
        }

        if (request.checkIn.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Check-in date must be in the future")
        }

        val bookingId = UUID.randomUUID().toString()
        val booking = BookingEntity(
            id = bookingId,
            member = member,
            checkIn = request.checkIn,
            checkOut = request.checkOut
        )

        concurrentBookingHashMap[bookingId] = booking

        return BookingResponse(
            id = booking.id,
            memberEmail = member.email,
            memberName = member.name,
            checkIn = booking.checkIn,
            checkOut = booking.checkOut
        )
    }

    fun amendBooking(bookingId: String, request: AmendBookingRequest): BookingResponse {
        val booking = concurrentBookingHashMap[bookingId]
            ?: throw IllegalArgumentException("Booking not found")

        if (request.checkIn.isAfter(request.checkOut)) {
            throw IllegalArgumentException("Check-in date must be before check-out date")
        }

        if (request.checkIn.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Check-in date must be in the future")
        }

        val updatedBooking = booking.copy(
            checkIn = request.checkIn,
            checkOut = request.checkOut
        )

        concurrentBookingHashMap[bookingId] = updatedBooking

        return BookingResponse(
            id = updatedBooking.id,
            memberEmail = updatedBooking.member.email,
            memberName = updatedBooking.member.name,
            checkIn = updatedBooking.checkIn,
            checkOut = updatedBooking.checkOut
        )
    }

    fun cancelBooking(bookingId: String) {
        val booking = concurrentBookingHashMap.remove(bookingId)
            ?: throw IllegalArgumentException("Booking not found")
    }

    fun getBooking(bookingId: String): BookingResponse {
        val booking = concurrentBookingHashMap[bookingId]
            ?: throw IllegalArgumentException("Booking not found")

        return BookingResponse(
            id = booking.id,
            memberEmail = booking.member.email,
            memberName = booking.member.name,
            checkIn = booking.checkIn,
            checkOut = booking.checkOut
        )
    }

    fun getAllBookings(): BookingListResponse {
        val bookings = concurrentBookingHashMap.values.map { booking ->
            BookingResponse(
                id = booking.id,
                memberEmail = booking.member.email,
                memberName = booking.member.name,
                checkIn = booking.checkIn,
                checkOut = booking.checkOut
            )
        }

        return BookingListResponse(bookings = bookings)
    }

    fun getBookingsByEmail(email: String): BookingListResponse {
        val bookings = concurrentBookingHashMap.values
            .filter { it.member.email == email }
            .map { booking ->
                BookingResponse(
                    id = booking.id,
                    memberEmail = booking.member.email,
                    memberName = booking.member.name,
                    checkIn = booking.checkIn,
                    checkOut = booking.checkOut
                )
            }

        return BookingListResponse(bookings = bookings)
    }
}