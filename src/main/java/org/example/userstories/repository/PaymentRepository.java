package org.example.userstories.repository;

import org.example.userstories.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query(
            """
            select p.status as status, count(*) as count from Payment p 
            group by p.status
            """
    )
    List<PaymentByStatus> countPaymentByStatus();
}
