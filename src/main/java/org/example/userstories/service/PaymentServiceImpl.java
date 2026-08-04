package org.example.userstories.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.example.userstories.dto.SavePayment;
import org.example.userstories.exception.PaymentNotFoundException;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.repository.PaymentByStatus;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.dto.PaymentUpdateRequest;
import org.example.userstories.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final EntityManager entityManager;

    @Override
    public Page<Payment> findAll(final PaymentStatus status,
                                       final Double minAmount, final Double maxAmount,
                                       Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Payment> cq = cb.createQuery(Payment.class);

        Root<Payment> root = cq.from(Payment.class);
        List<Predicate> predicates = buildPredicates(status, minAmount, maxAmount, cb, root);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Payment> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Payment> content = query.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Payment> countRoot = countQuery.from(Payment.class);
        List<Predicate> countPredicates = buildPredicates(status, minAmount, maxAmount,
                cb, countRoot);
        countQuery.select(cb.count(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));
         Long total = entityManager.createQuery(countQuery).getSingleResult();


        return new PageImpl<>(content, pageable, total);
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
                    cb.equal(root.get("status"), status)
            );
        }
        if (minAmount != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(root.get("amount"), minAmount)
            );
        }
        if (maxAmount != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(root.get("amount"), maxAmount)
            );
        }
        return predicates;
    }

    @Transactional
    @Override
    public SavePayment save(Payment payment, final String idempotencyKey) {
        Optional<Payment> paymentByIdempotencyKey = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if(paymentByIdempotencyKey.isPresent()) {
            return new SavePayment(paymentByIdempotencyKey.get(), true);
        } else {
            payment.setIdempotencyKey(idempotencyKey);
            return new SavePayment(paymentRepository.save(payment), false);
        }
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

    @Override
    public Map<PaymentStatus, Long> getStats() {
        return paymentRepository.countPaymentByStatus().stream()
                .collect(Collectors.toMap(
                        PaymentByStatus::getStatus, PaymentByStatus::getCount));
    }

    @Override
    public List<Payment> findAllForExport() {
        return paymentRepository.findAll();
    }
}
