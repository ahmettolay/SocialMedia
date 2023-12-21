package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.ForgotPasswordMailResponseDto;
import com.bilgeadam.dto.response.RegisterResponseDto;
import com.bilgeadam.exception.AuthManagerException;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.manager.IEmailManager;
import com.bilgeadam.manager.IUserProfileManager;
import com.bilgeadam.mapper.IAuthMapper;
import com.bilgeadam.rabbitmq.producer.RegisterMailProducer;
import com.bilgeadam.rabbitmq.producer.RegisterProducer;
import com.bilgeadam.repository.IAuthRepository;
import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.repository.entity.enums.ERole;
import com.bilgeadam.repository.entity.enums.EStatus;
import com.bilgeadam.utility.JwtTokenProvider;
import com.bilgeadam.utility.MD5Encoding;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.bilgeadam.utility.CodeGenerator.generateCode;

@Service
public class AuthService extends ServiceManager<Auth, Long> {
    private final IAuthRepository authRepository;
    private final IUserProfileManager userManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RegisterProducer registerProducer;
    private final RegisterMailProducer registerMailProducer;
    private final PasswordEncoder passwordEncoder;
    private final IEmailManager emailManager;

    public AuthService(JpaRepository<Auth, Long> repository,
                       IAuthRepository authRepository, IUserProfileManager userManager,
                       JwtTokenProvider jwtTokenProvider, RegisterProducer registerProducer,
                       RegisterMailProducer registerMailProducer, IEmailManager emailManager,
                       PasswordEncoder passwordEncoder) {
        super(repository);
        this.authRepository = authRepository;
        this.userManager = userManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.registerProducer = registerProducer;
        this.registerMailProducer = registerMailProducer;
        this.passwordEncoder = passwordEncoder;
        this.emailManager = emailManager;
    }

    public RegisterResponseDto registerMD5(RegisterRequestDto dto){
        Auth auth = IAuthMapper.INSTANCE.fromRequestDtoToAuth(dto);
        if (dto.getPassword().equals(dto.getRepassword())){
            auth.setActivationCode(generateCode());
            auth.setPassword(MD5Encoding.md5(dto.getPassword()));
            save(auth);
            String token = jwtTokenProvider.createToken(auth.getId(), auth.getRole()).get();
            userManager.createUser("Bearer " + token, IAuthMapper.INSTANCE.fromAuthToNewCreateUserDto(auth));
        }else {
            throw new AuthManagerException(ErrorType.PASSWORD_ERROR);
        }
        RegisterResponseDto responseDto = IAuthMapper.INSTANCE.fromAuthToResponseDto(auth);
        return responseDto;
    }

    public RegisterResponseDto registerWithRabbitMq(RegisterRequestDto dto){
        Auth auth = IAuthMapper.INSTANCE.fromRequestDtoToAuth(dto);
        if (dto.getPassword().equals(dto.getRepassword())){
            auth.setActivationCode(generateCode());
            auth.setPassword(passwordEncoder.encode(dto.getPassword()));
            save(auth);
            registerProducer.sendNewUser(IAuthMapper.INSTANCE.fromAuthToRegisterModel(auth));
            registerMailProducer.sendActivationCode(IAuthMapper.INSTANCE.fromAuthToRegisterMailModel(auth));
        }else {
            throw new AuthManagerException(ErrorType.PASSWORD_ERROR);
        }
        RegisterResponseDto responseDto = IAuthMapper.INSTANCE.fromAuthToResponseDto(auth);
        return responseDto;
    }

    public Boolean passwordChange(FromUserProfilePasswordChangeDto dto){
        Optional<Auth> auth = authRepository.findById(dto.getAuthId());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setPassword(dto.getPassword());
        authRepository.save(auth.get());
        return true;
    }
    public Boolean forgotPassword(String email, String username){
        Optional<Auth> auth = authRepository.findOptionalByEmail(email);
        if (auth.get().getStatus().equals(EStatus.ACTIVE)){
            if (auth.get().getUsername().equals(username)){
                //random password variable
                String randomPassword = UUID.randomUUID().toString();
                auth.get().setPassword(passwordEncoder.encode(randomPassword));
                save(auth.get());
                ForgotPasswordMailResponseDto dto = ForgotPasswordMailResponseDto.builder()
                        .password(randomPassword)
                        .email(email)
                        .build();
                emailManager.forgotPasswordMail(dto);
                UserProfileChangePasswordRequestDto userProfileDto = UserProfileChangePasswordRequestDto.builder()
                        .authId(auth.get().getId())
                        .password(auth.get().getPassword())
                        .build();
                userManager.forgotPassword(userProfileDto);
                return true;
            }else {
                throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
            }
        }else {
            if (auth.get().getStatus().equals(EStatus.DELETED)){
                throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
            }
            throw new AuthManagerException(ErrorType.ACTIVATE_CODE_ERROR);
        }
    }

    public Boolean activateStatus(ActivateRequestDto dto){
        Optional<Auth> auth = findById(dto.getId());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }else if (auth.get().getActivationCode().equals(dto.getActivateCode())) {
            auth.get().setStatus(EStatus.ACTIVE);
            update(auth.get());
            String token = jwtTokenProvider.createToken(auth.get().getId(), auth.get().getRole()).get();
            userManager.activateStatus("Bearer " + token);
            return true;
        }
        throw new AuthManagerException(ErrorType.ACTIVATE_CODE_ERROR);
    }


    public String login(LoginRequestDto dto){
        Optional<Auth> auth = authRepository.findOptionalByUsername(dto.getUsername());
        if (auth.isEmpty() || !passwordEncoder.matches(dto.getPassword(), auth.get().getPassword())){
            throw new AuthManagerException(ErrorType.LOGIN_ERROR);
        } else if (!auth.get().getStatus().equals(EStatus.ACTIVE)) {
            throw new AuthManagerException(ErrorType.ACTIVATE_CODE_ERROR);
        }
        return jwtTokenProvider.createToken(auth.get().getId(), auth.get().getRole())
                .orElseThrow(() -> {throw new AuthManagerException(ErrorType.TOKEN_NOT_CREATED);
                });
    }

    public String loginMD5(LoginRequestDto dto){
        String newPass = MD5Encoding.md5(dto.getPassword());
        System.out.println(newPass);
        Optional<Auth> auth = authRepository.findOptionalByUsernameAndPassword(dto.getUsername(), newPass);
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        return jwtTokenProvider.createToken(auth.get().getId(), auth.get().getRole())
                .orElseThrow(() -> {throw new AuthManagerException(ErrorType.TOKEN_NOT_CREATED);
                });
    }

    public Boolean update(UpdateEmailOrUsernameRequestDto dto){
        Optional<Auth> auth = authRepository.findById(dto.getAuthId());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        /*
        user-service'de UserService içerisindeki UserProfile parametresi ile çözüm için kullanılacak.
        auth.get().setUsername(dto.getUsername());
        auth.get().setEmail(dto.getEmail());
        */
        IAuthMapper.INSTANCE.updateUsernameOrEmail(dto, auth.get());
        update(auth.get());
        return true;
    }

    public Boolean delete(String token){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(token);
        if (authId.isEmpty()){
            throw new AuthManagerException(ErrorType.INVALID_TOKEN);
        }

        Optional<Auth> auth = authRepository.findById(authId.get());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }

        auth.get().setStatus(EStatus.DELETED);
        update(auth.get());
        userManager.delete(authId.get());
        return true;
    }

    //user, admin  --> USER, ADMIN
    public List<Long> findByRole(String role) {
        ERole roles = ERole.valueOf(role.toUpperCase(Locale.ENGLISH));
        return authRepository.findByRole(roles).stream().map(x -> x.getId()).collect(Collectors.toList());
    }
}
