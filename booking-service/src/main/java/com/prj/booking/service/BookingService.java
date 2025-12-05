package com.prj.booking.service;

import com.prj.booking.dto.BookingRequest;
import com.prj.booking.dto.BookingResponse;
import com.prj.booking.entity.Booking;
import com.prj.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for customer {} and flight {}", request.getCustomerId(), request.getFlightId());

        Booking booking = new Booking();
        booking.setCustomerId(request.getCustomerId());
        booking.setFlightId(request.getFlightId());
        booking.setNumberOfPassengers(request.getNumberOfPassengers());
        booking.setTotalPrice(request.getTotalPrice());
        booking.setDepartureDate(request.getDepartureDate());
        booking.setSeatNumbers(request.getSeatNumbers());
        booking.setNotes(request.getNotes());
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with ID: {}", savedBooking.getId());

        return mapToResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomerId(Long customerId) {
        log.info("Fetching bookings for customer: {}", customerId);
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByFlightId(Long flightId) {
        log.info("Fetching bookings for flight: {}", flightId);
        return bookingRepository.findByFlightId(flightId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse updateBookingStatus(Long id, Booking.BookingStatus status) {
        log.info("Updating booking {} status to {}", id, status);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} status updated to {}", id, status);

        return mapToResponse(updatedBooking);
    }

    public BookingResponse confirmBooking(Long id, String paymentId) {
        log.info("Confirming booking {} with payment ID: {}", id, paymentId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentId(paymentId);
        Booking confirmedBooking = bookingRepository.save(booking);
        log.info("Booking {} confirmed", id);

        return mapToResponse(confirmedBooking);
    }

    public BookingResponse cancelBooking(Long id) {
        log.info("Cancelling booking: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);
        log.info("Booking {} cancelled", id);

        return mapToResponse(cancelledBooking);
    }

    public void deleteBooking(Long id) {
        log.info("Deleting booking: {}", id);
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
        log.info("Booking {} deleted", id);
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setCustomerId(booking.getCustomerId());
        response.setFlightId(booking.getFlightId());
        response.setNumberOfPassengers(booking.getNumberOfPassengers());
        response.setStatus(booking.getStatus());
        response.setTotalPrice(booking.getTotalPrice());
        response.setBookingDate(booking.getBookingDate());
        response.setDepartureDate(booking.getDepartureDate());
        response.setSeatNumbers(booking.getSeatNumbers());
        response.setPaymentId(booking.getPaymentId());
        response.setNotes(booking.getNotes());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }
}

