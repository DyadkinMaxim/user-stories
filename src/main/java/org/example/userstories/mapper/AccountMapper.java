package org.example.userstories.mapper;

import org.example.userstories.dto.AccountResponse;
import org.example.userstories.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toResponse(Account account);
}
