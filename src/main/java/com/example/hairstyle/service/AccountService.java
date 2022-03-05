package com.example.hairstyle.service;

import com.example.hairstyle.entity.Account;
import com.example.hairstyle.entity.Permission;
import com.example.hairstyle.entity.Role;
import com.example.hairstyle.payload.request.*;
import com.example.hairstyle.security.service.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

public interface AccountService {
    ResponseEntity<?> saveUser(SignupRequest signupRequest, String siteUrl);
    Role saveRole(Role role);
    ResponseEntity addRoleToAccount(RoleAccountRequest roleAccountRequest);
    Account getAccount(String username);
    ResponseEntity getAccounts(String username, int page, int size);
    UserDetails findAccountByUsername(String username);
    PermissionRequest transferPermission(Permission permission);
    RoleRequest transferRole(Role role);
    UserDetailsImpl transferAccount(Account account);
    ResponseEntity verifyEmail(String verificationCode);
    ResponseEntity changePassword(PasswordChangeRequest passwordRequest, String username);
    ResponseEntity verifyEmailToRecoverPassword(String email);
    ResponseEntity recoverPassword(PasswordRecoveryRequest passwordRequest, String verificationCode);
    ResponseEntity refreshToken(RefreshTokenRequest refreshTokenRequest);
    ResponseEntity disableAccount(String username);
    ResponseEntity enableAccount(String username);
    ResponseEntity multiDelete(Set<Long> ids);
    ResponseEntity addAccount(AccountRequest accountRequest);
    ResponseEntity updateAccount(AccountRequest accountRequest);

}
