package com.prj.booking.service;

import com.prj.booking.dto.BookingRequest;
import com.prj.booking.dto.BookingResponse;
import com.prj.booking.entity.Booking;
import com.prj.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest bookingRequest;
    private Booking booking;
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
        bookingRequest.setNotes("Window seats preferred");

        booking = new Booking();
        booking.setId(bookingId);
        booking.setCustomerId(1L);
        booking.setFlightId(100L);
        booking.setNumberOfPassengers(2);
        booking.setTotalPrice(500.00);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());
        booking.setDepartureDate(LocalDateTime.now().plusDays(7));
        booking.setSeatNumbers("A1,A2");
        booking.setNotes("Window seats preferred");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBooking_Success() {
        // Given
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        BookingResponse response = bookingService.createBooking(bookingRequest);

        // Then
        assertNotNull(response);
        assertEquals(bookingId, response.getId());
        assertEquals(bookingRequest.getCustomerId(), response.getCustomerId());
        assertEquals(bookingRequest.getFlightId(), response.getFlightId());
        assertEquals(bookingRequest.getNumberOfPassengers(), response.getNumberOfPassengers());
        assertEquals(Booking.BookingStatus.PENDING, response.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testGetBookingById_Success() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When
        BookingResponse response = bookingService.getBookingById(bookingId);

        // Then
        assertNotNull(response);
        assertEquals(bookingId, response.getId());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetBookingById_NotFound() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> bookingService.getBookingById(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetAllBookings_Success() {
        // Given
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setCustomerId(2L);
        booking2.setFlightId(200L);
        booking2.setStatus(Booking.BookingStatus.CONFIRMED);
        
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking, booking2));

        // When
        List<BookingResponse> responses = bookingService.getAllBookings();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testGetBookingsByCustomerId_Success() {
        // Given
        Long customerId = 1L;
        when(bookingRepository.findByCustomerId(customerId)).thenReturn(Arrays.asList(booking));

        // When
        List<BookingResponse> responses = bookingService.getBookingsByCustomerId(customerId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(customerId, responses.get(0).getCustomerId());
        verify(bookingRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void testGetBookingsByFlightId_Success() {
        // Given
        Long flightId = 100L;
        when(bookingRepository.findByFlightId(flightId)).thenReturn(Arrays.asList(booking));

        // When
        List<BookingResponse> responses = bookingService.getBookingsByFlightId(flightId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(flightId, responses.get(0).getFlightId());
        verify(bookingRepository, times(1)).findByFlightId(flightId);
    }

    @Test
    void testUpdateBookingStatus_Success() {
        // Given
        Booking.BookingStatus newStatus = Booking.BookingStatus.CONFIRMED;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        BookingResponse response = bookingService.updateBookingStatus(bookingId, newStatus);

        // Then
        assertNotNull(response);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testUpdateBookingStatus_NotFound() {
        // Given
        Booking.BookingStatus newStatus = Booking.BookingStatus.CONFIRMED;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            bookingService.updateBookingStatus(bookingId, newStatus));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testConfirmBooking_Success() {
        // Given
        String paymentId = "PAY-12345";
        booking.setStatus(Booking.BookingStatus.PENDING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        BookingResponse response = bookingService.confirmBooking(bookingId, paymentId);

        // Then
        assertNotNull(response);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_Success() {
        // Given
        booking.setStatus(Booking.BookingStatus.PENDING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        BookingResponse response = bookingService.cancelBooking(bookingId);

        // Then
        assertNotNull(response);
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_AlreadyCancelled() {
        // Given
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testDeleteBooking_Success() {
        // Given
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(bookingId);

        // When
        bookingService.deleteBooking(bookingId);

        // Then
        verify(bookingRepository, times(1)).existsById(bookingId);
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    void testDeleteBooking_NotFound() {
        // Given
        when(bookingRepository.existsById(bookingId)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> bookingService.deleteBooking(bookingId));
        verify(bookingRepository, times(1)).existsById(bookingId);
        verify(bookingRepository, never()).deleteById(bookingId);
    }
}



