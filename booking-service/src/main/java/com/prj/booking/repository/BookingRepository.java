package com.prj.booking.repository;

import com.prj.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByFlightId(Long flightId);

    List<Booking> findByStatus(Booking.BookingStatus status);

    Optional<Booking> findByPaymentId(String paymentId);

    List<Booking> findByCustomerIdAndStatus(Long customerId, Booking.BookingStatus status);

    boolean existsByFlightIdAndSeatNumbers(Long flightId, String seatNumbers);
}

