package com.prj.booking.dto;

import com.prj.booking.entity.Booking;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "Number of passengers is required")
    @Min(value = 1, message = "Number of passengers must be at least 1")
    private Integer numberOfPassengers;

    @NotNull(message = "Total price is required")
    @Min(value = 0, message = "Total price must be positive")
    private Double totalPrice;

    private LocalDateTime departureDate;

    private String seatNumbers;

    private String notes;
}

