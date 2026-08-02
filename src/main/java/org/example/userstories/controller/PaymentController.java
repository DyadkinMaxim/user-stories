package org.example.userstories.controller;

import lombok.RequiredArgsConstructor;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentRequest;
import org.example.userstories.model.PaymentResponse;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.model.PaymentUpdateRequest;
import org.example.userstories.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount
    ) {
        return ResponseEntity.ok(
                paymentService.findAll(status, minAmount, maxAmount).stream()
                        .map(paymentMapper::toResponse)
                        .toList()
                );
    }

    @GetMapping("/search")
    public ResponseEntity<List<PaymentResponse>> search(
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount
    ) {
        return ResponseEntity.ok(
                paymentService.search(minAmount, maxAmount).stream()
                        .map(paymentMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok(
                paymentMapper.toResponse(
                        paymentService.findById(id))
                );
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        Payment saved = paymentService.save(paymentMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentMapper.toResponse(saved));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status) {
        Payment updated = paymentService.updateStatus(id, status);
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePaymentDetails(
            @PathVariable UUID id,
            @RequestBody PaymentUpdateRequest paymentUpdateRequest) {
        Payment updated = paymentService.updatePaymentDetails(id, paymentUpdateRequest);
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }

    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> deletePaymentForce(@PathVariable UUID id) {
        paymentService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/soft/{id}")
    public ResponseEntity<Void> deletePaymentSoft(@PathVariable UUID id) {
        paymentService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

}
