package com.prj.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.booking.dto.BookingRequest;
import com.prj.booking.dto.BookingResponse;
import com.prj.booking.entity.Booking;
import com.prj.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private Long bookingId;

    @BeforeEach
    void setUp() {
        bookingId = 1L;

        bookingRequest = new BookingRequest();
        bookingRequest.setCustomerId(1L);
        bookingRequest.setFlightId(100L);
        bookingRequest.setNumberOfPassengers(2);
        bookingRequest.setTotalPrice(500.00);
        bookingRequest.setDepartureDate(LocalDateTime.now().plusDays(7));
        bookingRequest.setSeatNumbers("A1,A2");

        bookingResponse = new BookingResponse();
        bookingResponse.setId(bookingId);
        bookingResponse.setCustomerId(1L);
        bookingResponse.setFlightId(100L);
        bookingResponse.setNumberOfPassengers(2);
        bookingResponse.setTotalPrice(500.00);
        bookingResponse.setStatus(Booking.BookingStatus.PENDING);
        bookingResponse.setBookingDate(LocalDateTime.now());
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        // Given
        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(bookingResponse);

        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.flightId").value(100L));

        verify(bookingService, times(1)).createBooking(any(BookingRequest.class));
    }

    @Test
    void testCreateBooking_ValidationError() throws Exception {
        // Given
        BookingRequest invalidRequest = new BookingRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any(BookingRequest.class));
    }

    @Test
    void testGetBookingById_Success() throws Exception {
        // Given
        when(bookingService.getBookingById(bookingId)).thenReturn(bookingResponse);

        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.customerId").value(1L));

        verify(bookingService, times(1)).getBookingById(bookingId);
    }

    @Test
    void testGetAllBookings_Success() throws Exception {
        // Given
        List<BookingResponse> responses = Arrays.asList(bookingResponse);
        when(bookingService.getAllBookings()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(bookingId));

        verify(bookingService, times(1)).getAllBookings();
    }

    @Test
    void testGetBookingsByCustomerId_Success() throws Exception {
        // Given
        Long customerId = 1L;
        List<BookingResponse> responses = Arrays.asList(bookingResponse);
        when(bookingService.getBookingsByCustomerId(customerId)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/bookings/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value(customerId));

        verify(bookingService, times(1)).getBookingsByCustomerId(customerId);
    }

    @Test
    void testGetBookingsByFlightId_Success() throws Exception {
        // Given
        Long flightId = 100L;
        List<BookingResponse> responses = Arrays.asList(bookingResponse);
        when(bookingService.getBookingsByFlightId(flightId)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/bookings/flight/{flightId}", flightId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flightId").value(flightId));

        verify(bookingService, times(1)).getBookingsByFlightId(flightId);
    }

    @Test
    void testUpdateBookingStatus_Success() throws Exception {
        // Given
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("status", "CONFIRMED");
        bookingResponse.setStatus(Booking.BookingStatus.CONFIRMED);
        when(bookingService.updateBookingStatus(eq(bookingId), any(Booking.BookingStatus.class)))
                .thenReturn(bookingResponse);

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/status", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(bookingService, times(1)).updateBookingStatus(eq(bookingId), any(Booking.BookingStatus.class));
    }

    @Test
    void testConfirmBooking_Success() throws Exception {
        // Given
        Map<String, String> paymentMap = new HashMap<>();
        paymentMap.put("paymentId", "PAY-12345");
        bookingResponse.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingResponse.setPaymentId("PAY-12345");
        when(bookingService.confirmBooking(bookingId, "PAY-12345")).thenReturn(bookingResponse);

        // When & Then
        mockMvc.perform(post("/api/bookings/{id}/confirm", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.paymentId").value("PAY-12345"));

        verify(bookingService, times(1)).confirmBooking(bookingId, "PAY-12345");
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        // Given
        bookingResponse.setStatus(Booking.BookingStatus.CANCELLED);
        when(bookingService.cancelBooking(bookingId)).thenReturn(bookingResponse);

        // When & Then
        mockMvc.perform(post("/api/bookings/{id}/cancel", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(bookingService, times(1)).cancelBooking(bookingId);
    }

    @Test
    void testDeleteBooking_Success() throws Exception {
        // Given
        doNothing().when(bookingService).deleteBooking(bookingId);

        // When & Then
        mockMvc.perform(delete("/api/bookings/{id}", bookingId))
                .andExpect(status().isNoContent());

        verify(bookingService, times(1)).deleteBooking(bookingId);
    }
}



