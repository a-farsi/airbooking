package com.prj.booking.dto;

import com.prj.booking.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long customerId;
    private Long flightId;
    private Integer numberOfPassengers;
    private Booking.BookingStatus status;
    private Double totalPrice;
    private LocalDateTime bookingDate;
    private LocalDateTime departureDate;
    private String seatNumbers;
    private String paymentId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

