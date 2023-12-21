package com.bilgeadam.controller;

import static com.bilgeadam.constant.ApiUrls.*;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.RegisterResponseDto;
import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.repository.entity.enums.ERole;
import com.bilgeadam.service.AuthService;
import com.bilgeadam.utility.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH)
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/registermd5")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto dto){
        return ResponseEntity.ok(authService.registerMD5(dto));
    }

    @PostMapping(REGISTER) //register2
    public ResponseEntity<RegisterResponseDto> registerWithRabbitMq(@RequestBody @Valid RegisterRequestDto dto){
        return ResponseEntity.ok(authService.registerWithRabbitMq(dto));
    }

    @PutMapping(PASSWORD_CHANGE)
    public ResponseEntity<Boolean> passwordChange(@RequestBody FromUserProfilePasswordChangeDto dto){
        return ResponseEntity.ok(authService.passwordChange(dto));
    }

    @PostMapping(FORGOT_PASSWORD)
    public ResponseEntity<Boolean> forgotPassword(String email, String username){
        return ResponseEntity.ok(authService.forgotPassword(email, username));
    }

    @PostMapping(ACTIVATE_STATUS)
    public ResponseEntity<Boolean> activateStatus(@RequestBody ActivateRequestDto dto){
        return ResponseEntity.ok(authService.activateStatus(dto));
    }
    @PostMapping(LOGIN)
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto){
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping(LOGIN + "Md5")
    public ResponseEntity<String> loginWithMD5(@RequestBody LoginRequestDto dto){
        return ResponseEntity.ok(authService.loginMD5(dto));
    }

    @GetMapping(FIND_ALL)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Auth>> findAll(){
        return ResponseEntity.ok(authService.findAll());
    }

    @DeleteMapping(DELETE_BY_ID)
    public ResponseEntity<Boolean> delete(String token){
        return ResponseEntity.ok(authService.delete(token));
    }

    @Hidden
    @PutMapping("/update-username-email")
    public ResponseEntity<Boolean> update(@RequestBody UpdateEmailOrUsernameRequestDto dto){
        return ResponseEntity.ok(authService.update(dto));
    }

    @GetMapping("/create-token-with-id")
    public ResponseEntity<String> createToken(Long id){
        return ResponseEntity.ok(tokenProvider.createToken(id).get());
    }

    @GetMapping("/create-token-with-role")
    public ResponseEntity<String> createToken(Long id, ERole role){
        return ResponseEntity.ok(tokenProvider.createToken(id, role).get());
    }

    @GetMapping("/get-id-from-token")
    public ResponseEntity<Long> getIdFromToken(String token){
        return ResponseEntity.ok(tokenProvider.getIdFromToken(token).get());
    }

    @GetMapping("/get-role-from-token")
    public ResponseEntity<String> getRoleFromToken(String token){
        return ResponseEntity.ok(tokenProvider.getRoleFromToken(token).get());
    }

    @GetMapping("/find-by-role/{role}")
    public ResponseEntity<List<Long>> findByRole(@RequestHeader(value = "Authorization")String token, @PathVariable String role){
        return ResponseEntity.ok(authService.findByRole(role));
    }
}
