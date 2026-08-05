package org.example.userstories.mapper;

import org.example.userstories.dto.AccountResponse;
import org.example.userstories.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PaymentMapper.class)
public interface AccountMapper {

    @Mapping(target = "payments", source = "payments")
    AccountResponse toResponse(Account account);
}
