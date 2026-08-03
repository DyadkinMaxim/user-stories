package org.example.userstories.mapper;

import org.example.userstories.model.Payment;
import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.dto.PaymentResponse;
import org.example.userstories.dto.PaymentUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
    void updateEntity(PaymentUpdateRequest request, @MappingTarget Payment payment);
}
