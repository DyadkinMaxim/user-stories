package org.example.userstories.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.userstories.dto.PagedResponse;
import org.example.userstories.dto.SavePayment;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.dto.PaymentResponse;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.dto.PaymentUpdateRequest;
import org.example.userstories.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @GetMapping
    public ResponseEntity<PagedResponse<PaymentResponse>> findAll(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Payment> allPayments = paymentService.findAll(
                status, minAmount, maxAmount, pageable);
        PagedResponse<PaymentResponse> response =
                new PagedResponse<>(
                        allPayments.stream().map(paymentMapper::toResponse).toList(),
                        page,
                        size,
                        allPayments.getTotalElements(),
                        allPayments.getTotalPages(),
                        allPayments.isLast()
                );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok(paymentMapper.toResponse(paymentService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @RequestBody @Valid PaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        SavePayment saved = paymentService.save(paymentMapper.toEntity(request), idempotencyKey);
        return ResponseEntity.status(
                saved.isDuplicate() ? HttpStatus.OK : HttpStatus.CREATED)
                .body(paymentMapper.toResponse(saved.payment())
                );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable UUID id, @RequestParam PaymentStatus status) {
        Payment updated = paymentService.updateStatus(id, status);
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePaymentDetails(
            @PathVariable UUID id,
            @RequestBody @Valid PaymentUpdateRequest paymentUpdateRequest) {
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

    @GetMapping("/stats")
    public ResponseEntity<Map<PaymentStatus, Long>> getStats() {
        return ResponseEntity.ok(paymentService.getStats());
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=payments.csv");

        PrintWriter writer = response.getWriter();
        writer.println("id,amount,currency,toIban,status,createdAt");

        paymentService.findAllForExport().forEach(payment ->
                writer.println(String.join(",",
                        payment.getId().toString(),
                        payment.getAmount().toString(),
                        payment.getCurrency(),
                        payment.getToIban(),
                        payment.getStatus().toString(),
                        payment.getCreatedAt().toString()
                ))
        );

        writer.flush();
    }

}
