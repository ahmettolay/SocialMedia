package com.bilgeadam.manager;

import com.bilgeadam.dto.response.ForgotPasswordMailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.bilgeadam.constant.ApiUrls.*;

@FeignClient(url = "http://localhost:8085/api/v1/mail", name = "auth-mail")
public interface IEmailManager {
    @PostMapping(FORGOT_PASSWORD)
    public ResponseEntity<Boolean> forgotPasswordMail(@RequestBody ForgotPasswordMailResponseDto dto);
}
