package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.NewCreateUserRequestDto;
import com.bilgeadam.dto.request.RegisterRequestDto;
import com.bilgeadam.dto.request.UpdateEmailOrUsernameRequestDto;
import com.bilgeadam.dto.response.RegisterResponseDto;
import com.bilgeadam.rabbitmq.model.RegisterMailModel;
import com.bilgeadam.rabbitmq.model.RegisterModel;
import com.bilgeadam.repository.entity.Auth;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IAuthMapper {
    IAuthMapper INSTANCE = Mappers.getMapper(IAuthMapper.class);

    Auth fromRequestDtoToAuth(final RegisterRequestDto dto);

    @Mapping(source = "id", target = "authId")
    RegisterModel fromAuthToRegisterModel(final Auth auth);

    RegisterMailModel fromAuthToRegisterMailModel(final Auth auth);

    RegisterResponseDto fromAuthToResponseDto(final Auth auth);

    @Mapping(source = "id", target = "authId")
    NewCreateUserRequestDto fromAuthToNewCreateUserDto(final Auth auth);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUsernameOrEmail(UpdateEmailOrUsernameRequestDto dto, @MappingTarget Auth auth);
}
