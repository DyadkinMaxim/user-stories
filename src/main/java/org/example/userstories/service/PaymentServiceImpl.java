package org.example.userstories.service;

import lombok.RequiredArgsConstructor;
import org.example.userstories.exception.PaymentNotFoundException;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findAll(PaymentStatus status) {
        List<Payment> payments;
        if(status == null) {
            payments = paymentRepository.findAll();
        } else {
            payments = paymentRepository.findAllByStatus(status);
        }
        return payments;
    }

    @Transactional
    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment findById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    @Transactional
    @Override
    public Payment updateStatus(UUID id, PaymentStatus status) {
        Payment payment = findById(id);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }
}
