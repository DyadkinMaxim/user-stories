package org.example.userstories.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.example.userstories.exception.PaymentNotFoundException;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.model.PaymentUpdateRequest;
import org.example.userstories.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final EntityManager entityManager;

    @Override
    public List<Payment> findAll(final PaymentStatus status,
                                 final Double minAmount, final Double maxAmount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Payment> cq = cb.createQuery(Payment.class);

        Root<Payment> root = cq.from(Payment.class);
        List<Predicate> predicates = buildPredicates(status, minAmount, maxAmount, cb, root);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Payment> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    private List<Predicate> buildPredicates(
            final PaymentStatus status,
            final Double minAmount, final Double maxAmount,
            CriteriaBuilder cb,
            Root<Payment> root
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(
                    cb.equal(cb.lower(root.get("status")),
                            status.toString().toLowerCase())
            );
        }

        if (minAmount != null && maxAmount != null) {
            predicates.add(
                    cb.between(root.get("amount"), minAmount, maxAmount)
            );
        }
        return predicates;
    }

    @Override
    public List<Payment> search(final Double minAmount, final Double maxAmount) {
      return paymentRepository.searchPaymentByAmountBetween(minAmount, maxAmount);
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
        paymentMapper.updateEntity(details, paymentById);
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
