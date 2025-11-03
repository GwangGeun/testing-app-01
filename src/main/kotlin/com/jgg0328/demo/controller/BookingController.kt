package com.jgg0328.demo.controller

import com.jgg0328.demo.dto.AmendBookingRequest
import com.jgg0328.demo.dto.BookingListResponse
import com.jgg0328.demo.dto.BookingResponse
import com.jgg0328.demo.dto.CreateBookingRequest
import com.jgg0328.demo.service.BookingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/bookings")
class BookingController(
    val bookingService: BookingService
) {

    @PostMapping
    fun createBooking(@RequestBody request: CreateBookingRequest): ResponseEntity<BookingResponse> {
        return try {
            val response = bookingService.createBooking(request)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{bookingId}")
    fun amendBooking(
        @PathVariable bookingId: String,
        @RequestBody request: AmendBookingRequest
    ): ResponseEntity<BookingResponse> {
        return try {
            val response = bookingService.amendBooking(bookingId, request)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{bookingId}")
    fun cancelBooking(@PathVariable bookingId: String): ResponseEntity<Void> {
        return try {
            bookingService.cancelBooking(bookingId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{bookingId}")
    fun getBooking(@PathVariable bookingId: String): ResponseEntity<BookingResponse> {
        return try {
            val response = bookingService.getBooking(bookingId)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping
    fun getAllBookings(@RequestParam(required = false) email: String?): ResponseEntity<BookingListResponse> {
        return try {
            val response = if (email != null) {
                bookingService.getBookingsByEmail(email)
            } else {
                bookingService.getAllBookings()
            }
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

}