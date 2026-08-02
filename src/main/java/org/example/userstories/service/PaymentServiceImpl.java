package org.example.userstories.service;

import lombok.RequiredArgsConstructor;
import org.example.userstories.exception.PaymentNotFoundException;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.model.PaymentUpdateRequest;
import org.example.userstories.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<Payment> findAll(PaymentStatus status) {
        return status == null
                ? paymentRepository.findAll()
                : paymentRepository.findAllByStatus(status);
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

    @Transactional
    @Override
    public Payment updatePaymentDetails(UUID id, PaymentUpdateRequest details) {
        Payment paymentById = findById(id);
        paymentMapper.toEntity(details, paymentById);
        return paymentRepository.save(paymentById);
    }

    @Override
    @Transactional
    public void hardDelete(UUID id) {
        findById(id);
        paymentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        updateStatus(id, PaymentStatus.CANCELLED);
    }
}
