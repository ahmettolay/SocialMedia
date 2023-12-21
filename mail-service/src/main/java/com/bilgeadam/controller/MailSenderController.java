package com.bilgeadam.controller;

import com.bilgeadam.dto.ForgotPasswordMailResponseDto;
import com.bilgeadam.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/mail")
@RestController
@RequiredArgsConstructor
public class MailSenderController {
    private final MailSenderService mailSenderService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Boolean> forgotPasswordMail(@RequestBody ForgotPasswordMailResponseDto dto){
        return ResponseEntity.ok(mailSenderService.sendMailForgotPassword(dto));
    }
}
