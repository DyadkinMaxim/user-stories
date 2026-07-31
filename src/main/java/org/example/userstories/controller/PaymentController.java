package org.example.userstories.controller;

import lombok.RequiredArgsConstructor;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentRequest;
import org.example.userstories.model.PaymentResponse;
import org.example.userstories.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public ResponseEntity<List<PaymentResponse>> findAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.findAll().stream()
                        .map(paymentMapper::toResponse)
                        .toList()
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable final UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentMapper.toResponse(
                        paymentService.findById(id))
                );
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        Payment saved = paymentService.save(paymentMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentMapper.toResponse(saved)
                );
    }
}
