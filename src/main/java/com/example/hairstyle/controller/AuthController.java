package com.example.hairstyle.controller;

import com.example.hairstyle.payload.request.*;
import com.example.hairstyle.security.jwt.JwtService;
import com.example.hairstyle.service.AccountService;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountService accountService;

    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {
        return accountService.saveUser(signUpRequest, getSiteUrl(request));
    }

    @GetMapping("/verify")
    public ResponseEntity verifyEmail(@Param("code") String code) {
        return accountService.verifyEmail(code);
    }

    @PostMapping("/password/change")
    public ResponseEntity passwordRecovery(@Valid @RequestBody PasswordChangeRequest passwordRequest, HttpServletRequest request ) {
        return accountService.changePassword(passwordRequest, getUsernameFromToken(request));
    }

    @GetMapping("/password/forget")
    public ResponseEntity verifyEmailToRecoverPassword(@RequestParam String email) {
        return accountService.verifyEmailToRecoverPassword(email);
    }

    @PostMapping("/password/recovery")
    public ResponseEntity recoverPassword(@Param("code") String code, @Valid @RequestBody PasswordRecoveryRequest passwordRequest) {
        return accountService.recoverPassword(passwordRequest, code);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshAccessToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return accountService.refreshToken(refreshTokenRequest);
    }

    private String getSiteUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/disable")
    public ResponseEntity disableAccount(@RequestParam String username) {
        return accountService.disableAccount(username);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/enable")
    public ResponseEntity enableAccount(@RequestParam String username) {
        return accountService.enableAccount(username);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/addRoleToAccount")
    public ResponseEntity addRoleToAccount(@Valid @RequestBody RoleAccountRequest roleAccountRequest){
        return accountService.addRoleToAccount(roleAccountRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/multiDelete")
    public ResponseEntity multiDelete(@Valid @RequestBody Long[] ids){
        var idSet = new HashSet<>(Arrays.asList(ids));
        return accountService.multiDelete(idSet);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity addAccount(@Valid @RequestBody AccountRequest accountRequest){
        return accountService.addAccount(accountRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity updateAccount(@RequestBody AccountRequest accountRequest){
        return accountService.updateAccount(accountRequest);
    }

    private String getUsernameFromToken(HttpServletRequest request) {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        return jwtService.getUsernameFromJwtToken(token);
    }
}