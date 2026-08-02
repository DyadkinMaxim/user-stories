package org.example.userstories.mapper;

import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentRequest;
import org.example.userstories.model.PaymentResponse;
import org.example.userstories.model.PaymentUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Payment toEntity(PaymentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Payment toEntity(PaymentUpdateRequest request);
}
