package org.example.userstories.repository;

import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findAllByStatus(final PaymentStatus status);

    List<Payment> searchPaymentByAmountBetween(final Double minAmount, final Double maxAmount);
}
