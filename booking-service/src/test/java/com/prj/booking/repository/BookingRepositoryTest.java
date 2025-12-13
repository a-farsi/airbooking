package com.prj.booking.repository;

import com.prj.booking.entity.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        booking1 = new Booking();
        booking1.setCustomerId(1L);
        booking1.setFlightId(100L);
        booking1.setNumberOfPassengers(2);
        booking1.setTotalPrice(500.00);
        booking1.setStatus(Booking.BookingStatus.PENDING);
        booking1.setBookingDate(LocalDateTime.now());
        booking1.setCreatedAt(LocalDateTime.now());
        booking1.setUpdatedAt(LocalDateTime.now());

        booking2 = new Booking();
        booking2.setCustomerId(1L);
        booking2.setFlightId(200L);
        booking2.setNumberOfPassengers(1);
        booking2.setTotalPrice(300.00);
        booking2.setStatus(Booking.BookingStatus.CONFIRMED);
        booking2.setBookingDate(LocalDateTime.now());
        booking2.setCreatedAt(LocalDateTime.now());
        booking2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testSaveBooking() {
        // When
        Booking saved = bookingRepository.save(booking1);

        // Then
        assertNotNull(saved.getId());
        assertEquals(booking1.getCustomerId(), saved.getCustomerId());
        assertEquals(booking1.getFlightId(), saved.getFlightId());
    }

    @Test
    void testFindById() {
        // Given
        Booking saved = entityManager.persistAndFlush(booking1);

        // When
        Optional<Booking> found = bookingRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testFindByCustomerId() {
        // Given
        entityManager.persistAndFlush(booking1);
        entityManager.persistAndFlush(booking2);
        Booking booking3 = new Booking();
        booking3.setCustomerId(2L);
        booking3.setFlightId(300L);
        booking3.setNumberOfPassengers(1);
        booking3.setTotalPrice(250.00);
        booking3.setStatus(Booking.BookingStatus.PENDING);
        booking3.setBookingDate(LocalDateTime.now());
        booking3.setCreatedAt(LocalDateTime.now());
        booking3.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(booking3);

        // When
        List<Booking> bookings = bookingRepository.findByCustomerId(1L);

        // Then
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().allMatch(b -> b.getCustomerId().equals(1L)));
    }

    @Test
    void testFindByFlightId() {
        // Given
        entityManager.persistAndFlush(booking1);
        entityManager.persistAndFlush(booking2);

        // When
        List<Booking> bookings = bookingRepository.findByFlightId(100L);

        // Then
        assertEquals(1, bookings.size());
        assertEquals(100L, bookings.get(0).getFlightId());
    }

    @Test
    void testFindByStatus() {
        // Given
        entityManager.persistAndFlush(booking1);
        entityManager.persistAndFlush(booking2);

        // When
        List<Booking> pendingBookings = bookingRepository.findByStatus(Booking.BookingStatus.PENDING);
        List<Booking> confirmedBookings = bookingRepository.findByStatus(Booking.BookingStatus.CONFIRMED);

        // Then
        assertEquals(1, pendingBookings.size());
        assertEquals(1, confirmedBookings.size());
    }

    @Test
    void testFindByPaymentId() {
        // Given
        booking1.setPaymentId("PAY-12345");
        Booking saved = entityManager.persistAndFlush(booking1);

        // When
        Optional<Booking> found = bookingRepository.findByPaymentId("PAY-12345");

        // Then
        assertTrue(found.isPresent());
        assertEquals("PAY-12345", found.get().getPaymentId());
    }

    @Test
    void testFindByCustomerIdAndStatus() {
        // Given
        entityManager.persistAndFlush(booking1);
        entityManager.persistAndFlush(booking2);

        // When
        List<Booking> pendingBookings = bookingRepository.findByCustomerIdAndStatus(1L, Booking.BookingStatus.PENDING);

        // Then
        assertEquals(1, pendingBookings.size());
        assertEquals(Booking.BookingStatus.PENDING, pendingBookings.get(0).getStatus());
    }

    @Test
    void testExistsByFlightIdAndSeatNumbers() {
        // Given
        booking1.setSeatNumbers("A1,A2");
        entityManager.persistAndFlush(booking1);

        // When
        boolean exists = bookingRepository.existsByFlightIdAndSeatNumbers(100L, "A1,A2");
        boolean notExists = bookingRepository.existsByFlightIdAndSeatNumbers(100L, "B1,B2");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testDeleteBooking() {
        // Given
        Booking saved = entityManager.persistAndFlush(booking1);
        Long id = saved.getId();

        // When
        bookingRepository.deleteById(id);

        // Then
        Optional<Booking> found = bookingRepository.findById(id);
        assertFalse(found.isPresent());
    }
}



