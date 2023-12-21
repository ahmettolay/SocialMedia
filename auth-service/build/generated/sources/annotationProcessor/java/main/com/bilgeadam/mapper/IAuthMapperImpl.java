package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.NewCreateUserRequestDto;
import com.bilgeadam.dto.request.RegisterRequestDto;
import com.bilgeadam.dto.request.UpdateEmailOrUsernameRequestDto;
import com.bilgeadam.dto.response.RegisterResponseDto;
import com.bilgeadam.rabbitmq.model.RegisterMailModel;
import com.bilgeadam.rabbitmq.model.RegisterModel;
import com.bilgeadam.repository.entity.Auth;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-30T02:02:20+0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.6 (Amazon.com Inc.)"
)
@Component
public class IAuthMapperImpl implements IAuthMapper {

    @Override
    public Auth fromRequestDtoToAuth(RegisterRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Auth.AuthBuilder<?, ?> auth = Auth.builder();

        auth.username( dto.getUsername() );
        auth.email( dto.getEmail() );
        auth.password( dto.getPassword() );

        return auth.build();
    }

    @Override
    public RegisterModel fromAuthToRegisterModel(Auth auth) {
        if ( auth == null ) {
            return null;
        }

        RegisterModel.RegisterModelBuilder registerModel = RegisterModel.builder();

        registerModel.authId( auth.getId() );
        registerModel.username( auth.getUsername() );
        registerModel.email( auth.getEmail() );
        registerModel.password( auth.getPassword() );

        return registerModel.build();
    }

    @Override
    public RegisterMailModel fromAuthToRegisterMailModel(Auth auth) {
        if ( auth == null ) {
            return null;
        }

        RegisterMailModel.RegisterMailModelBuilder registerMailModel = RegisterMailModel.builder();

        registerMailModel.username( auth.getUsername() );
        registerMailModel.email( auth.getEmail() );
        registerMailModel.activationCode( auth.getActivationCode() );

        return registerMailModel.build();
    }

    @Override
    public RegisterResponseDto fromAuthToResponseDto(Auth auth) {
        if ( auth == null ) {
            return null;
        }

        RegisterResponseDto.RegisterResponseDtoBuilder registerResponseDto = RegisterResponseDto.builder();

        registerResponseDto.id( auth.getId() );
        registerResponseDto.username( auth.getUsername() );
        registerResponseDto.activationCode( auth.getActivationCode() );

        return registerResponseDto.build();
    }

    @Override
    public NewCreateUserRequestDto fromAuthToNewCreateUserDto(Auth auth) {
        if ( auth == null ) {
            return null;
        }

        NewCreateUserRequestDto.NewCreateUserRequestDtoBuilder newCreateUserRequestDto = NewCreateUserRequestDto.builder();

        newCreateUserRequestDto.authId( auth.getId() );
        newCreateUserRequestDto.username( auth.getUsername() );
        newCreateUserRequestDto.email( auth.getEmail() );
        newCreateUserRequestDto.password( auth.getPassword() );

        return newCreateUserRequestDto.build();
    }

    @Override
    public void updateUsernameOrEmail(UpdateEmailOrUsernameRequestDto dto, Auth auth) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getUsername() != null ) {
            auth.setUsername( dto.getUsername() );
        }
        if ( dto.getEmail() != null ) {
            auth.setEmail( dto.getEmail() );
        }
    }
}
