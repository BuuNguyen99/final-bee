package com.example.hairstyle.service.implementation;

import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.entity.*;
import com.example.hairstyle.payload.request.*;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.payload.response.PagingResponse;
import com.example.hairstyle.repository.AccountRepository;
import com.example.hairstyle.repository.PermissionRepository;
import com.example.hairstyle.repository.RoleRepository;
import com.example.hairstyle.security.jwt.JwtService;
import com.example.hairstyle.security.service.UserDetailsImpl;
import com.example.hairstyle.service.AccountService;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    private final PermissionRepository permissionRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    private final JwtService jwtService;

    @Value("${app.cors.allowedOrigins}")
    private String[] allowedOrigins;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${service.mail.name}")
    private String nameSender;

    @Transactional
    public UserDetails findAccountByUsername(String username) {
        if (Strings.isNullOrEmpty(username)) {
            return null;
        }
        var accountOptional = accountRepository.findByUsername(username);

        return accountOptional.map(this::transferAccount).orElse(null);

    }

    @Transactional
    public ResponseEntity saveUser(SignupRequest signupRequest, String siteUrl) {
        if (Boolean.TRUE.equals(accountRepository.existsAccountByUsername(signupRequest.getUsername()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_ACCOUNT_ERROR));
        }

        if (Boolean.TRUE.equals(accountRepository.existsAccountByEmail(signupRequest.getEmail()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_EMAIL_ERROR));
        }

        if (!signupRequest.getPassword().equals(signupRequest.getConfirmedPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.PASSWORD_MISMATCH_ERROR));
        }

        Set<Role> rolesDb = new HashSet<>();

        if (CollectionUtils.isEmpty(signupRequest.getRoles())) {
            Role role = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            rolesDb.add(role);

        } else {
            Set<Role> roles = roleRepository.findRoleByIds(signupRequest.getRoles());
            if (roles.isEmpty()) {
                var role = roleRepository.findByName("USER")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                rolesDb.add(role);
            } else {
                rolesDb.addAll(roles);
            }
        }

        var verificationCode = RandomString.make(64);

        var profile = new Profile();
        profile.setFirstname(signupRequest.getFirstname());
        profile.setLastname(signupRequest.getLastname());
        profile.setCart(new Cart());

        Account account = new Account();
        account.setUsername(signupRequest.getUsername());
        account.setEmail(signupRequest.getEmail());
        account.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        account.setRoles(rolesDb);
        account.setVerificationCode(verificationCode);
        account.setEnabled(false);
        account.setProfile(profile);

        accountRepository.save(account);

        sendVerificationEmail(account, siteUrl);

        return ResponseEntity
                .ok(new MessageResponse(true, ResponseText.VERIFY_EMAIL));
    }

    public PermissionRequest transferPermission(Permission permission) {
        return new PermissionRequest(permission.getName(), permission.getCodename());
    }

    public RoleRequest transferRole(Role role) {
        Set<PermissionRequest> permissions = role.getPermissions()
                .stream()
                .map(this::transferPermission)
                .collect(Collectors.toSet());

        return new RoleRequest(role.getName(), permissions);
    }

    public UserDetailsImpl transferAccount(Account account) {
        Set<GrantedAuthority> authorities = account.getRoles()
                .stream()
                .map(this::transferRole)
                .map(RoleRequest::getSimpleGrantedAuthorities)
                .flatMap(Set<SimpleGrantedAuthority>::stream)
                .collect(Collectors.toSet());

        return new UserDetailsImpl(authorities,
                account.getPassword(),
                account.getUsername(),
                true,
                !account.getIsDeleted(),
                true,
                account.getEnabled());

    }

    @Override
    public ResponseEntity verifyEmail(String verificationCode) {
        if (!StringUtils.hasText(verificationCode)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.INVALID_VERIFICATION_ERROR));
        }

        var accountOptional = accountRepository.findByVerificationCode(verificationCode);
        if (accountOptional.isEmpty() || accountOptional.get().getEnabled()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.INVALID_VERIFICATION_ERROR));
        }

        var account = accountOptional.get();
        account.setVerificationCode(null);
        account.setEnabled(true);

        accountRepository.save(account);

        return ResponseEntity.ok(account);
    }

    @Override
    @Transactional
    public ResponseEntity verifyEmailToRecoverPassword(String email) {
        // check username
        var account = accountRepository.findByEmail(email);

        if (account.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_EMAIL_ERROR));
        }

        var verificationCode = RandomString.make(64);

        account.get().setVerificationCode(verificationCode);

        var subject = "Please verify your password recovery";
        var content = "Dear user,<br>"
                + "Please click the link below to verify your password recovery:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Support team";

        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(fromAddress, nameSender);
            helper.setTo(email);
            helper.setSubject(subject);

            var verifyUrl = allowedOrigins[0] + "/auth/reset-password?code=" + verificationCode;

            content = content.replace("[[URL]]", verifyUrl);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Message errors: {0}", e);
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding: {0}", e);
        }

        return ResponseEntity.ok().body(new MessageResponse(true, ResponseText.VERIFY_PASSWORD_RECOVERY));
    }

    @Transactional
    public ResponseEntity recoverPassword(PasswordRecoveryRequest passwordRequest, String verificationCode) {
        if (!StringUtils.hasText(verificationCode)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.INVALID_VERIFICATION_ERROR));
        }

        var accountOptional = accountRepository.findByVerificationCode(verificationCode);

        if (accountOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.INVALID_VERIFICATION_ERROR));
        }

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmedPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.PASSWORD_MISMATCH_ERROR));
        }

        var account = accountOptional.get();
        account.setVerificationCode(null);
        account.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        accountRepository.save(account);

        return ResponseEntity
                .ok()
                .body(new MessageResponse(true, ResponseText.CHANGE_PASSWORD_SUCCESSFULLY));
    }

    private void sendVerificationEmail(Account account, String siteUrl) {
        var toAddress = account.getEmail();
        var subject = "Please verify your registration";
        var content = "Dear user,<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Support team";

        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(fromAddress, nameSender);
            helper.setTo(toAddress);
            helper.setSubject(subject);

            var verifyUrl = siteUrl + "/api/auth/verify?code=" + account.getVerificationCode();

            content = content.replace("[[URL]]", verifyUrl);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Message errors: {0}", e);
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding: {0}", e);
        }

    }

    @Override
    @Transactional
    public ResponseEntity changePassword(PasswordChangeRequest passwordRequest, String username) {
        var account = accountRepository.findByUsername(username);

        if (account.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_ACCOUNT_ERROR));

        }

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmedPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.PASSWORD_MISMATCH_ERROR));
        }

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), account.get().getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.INCORRECT_PASSWORD_ERROR));
        }

        account.get().setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        accountRepository.save(account.get());

        return ResponseEntity.ok().body(new MessageResponse(true, ResponseText.CHANGE_PASSWORD_SUCCESSFULLY));
    }

    @Override
    public Role saveRole(Role role) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity addRoleToAccount(RoleAccountRequest roleAccountRequest) {
        var accountOptional = accountRepository.findByUsername(roleAccountRequest.getUsername());

        if (accountOptional.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_ACCOUNT_ERROR));
        }

        var roleOptional = roleRepository.findById(roleAccountRequest.getRoleId());

        if (roleOptional.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_ROLE_ERROR));
        }

        var account = accountOptional.get();
        var role = roleOptional.get();
        account.getRoles().add(role);

        accountRepository.save(account);
        return ResponseEntity
                .ok()
                .body(account);
    }

    @Override
    public Account getAccount(String username) {
        return null;
    }

    @Override
    public ResponseEntity getAccounts(String username, int page, int size) {
        var paging = PageRequest.of(page, size);

        Page<Account> accountPages;

        if (StringUtils.hasText(username)) {
            accountPages = accountRepository.findByUsernameContaining(username, paging);
        } else {
            accountPages = accountRepository.findAll(paging);
        }

        if (accountPages.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse(true, ResponseText.NO_DATA_RETRIEVAL));
        }

        var resultList = new PagingResponse<Account>();
        resultList.setContent(accountPages.getContent());
        resultList.setCurrentPage(accountPages.getNumber());
        resultList.setTotalItems(accountPages.getTotalElements());
        resultList.setTotalPages(accountPages.getTotalPages());

        return ResponseEntity
                .ok()
                .body(resultList);
    }

    @Override
    public ResponseEntity refreshToken(RefreshTokenRequest refreshTokenRequest) {
        var isValid = jwtService.validateToken(refreshTokenRequest.getRefreshToken());
        if (!isValid) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.INVALID_REFRESH_TOKEN));
        }

        var username = jwtService.getUsernameFromJwtToken(refreshTokenRequest.getRefreshToken());
        var userDetail = (UserDetailsImpl) findAccountByUsername(username);
        var accessToken = jwtService.generateJwtAccessToken(userDetail);
        return ResponseEntity
                .ok()
                .body(new AbstractMap.SimpleEntry<>("accessToken", accessToken) {
                });
    }

    @Override
    @Transactional
    public ResponseEntity disableAccount(String username) {
        var accountOptional = accountRepository.findByUsername(username);

        if (accountOptional.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_ACCOUNT_ERROR));
        }

        var account = accountOptional.get();
        account.setEnabled(false);

        accountRepository.save(account);

        return ResponseEntity
                .ok()
                .body(new MessageResponse(true, ResponseText.DISABLE_ACCOUNT_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ResponseEntity enableAccount(String username) {
        var accountOptional = accountRepository.findByUsername(username);

        if (accountOptional.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_ACCOUNT_ERROR));
        }

        var account = accountOptional.get();
        account.setEnabled(true);

        accountRepository.save(account);

        return ResponseEntity
                .ok()
                .body(new MessageResponse(true, ResponseText.ENABLE_ACCOUNT_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ResponseEntity multiDelete(Set<Long> ids) {
        var accounts = accountRepository.findAllById(ids);

        if (accounts.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NO_DATA_RETRIEVAL));
        }

        accountRepository.deleteAll(accounts);

        return ResponseEntity
                .ok()
                .body(new MessageResponse(false,ResponseText.DELETE_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ResponseEntity addAccount(AccountRequest request) {
        var isExistedUsername = accountRepository.existsAccountByUsername(request.getUsername());
        if(isExistedUsername){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_ACCOUNT_ERROR));
        }

        var isExistedEmail = accountRepository.existsAccountByEmail(request.getEmail());
        if(isExistedEmail){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_EMAIL_ERROR));
        }

        if (!request.getPassword().equals(request.getConfirmedPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.PASSWORD_MISMATCH_ERROR));
        }

        var roleOptional = roleRepository.findByName("USER");
        var account  = new Account();
        var profile = new Profile();
        roleOptional.ifPresent(role -> account.getRoles().add(role));
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setEnabled(request.getEnabled());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        profile.setFirstname(request.getFirstname());
        profile.setLastname(request.getLastname());

        if(Objects.nonNull(request.getGender())){
            profile.setGender(request.getGender());
        }

        if(Objects.nonNull(request.getMobile())){
            profile.setMobile(request.getMobile());
        }

        if(Objects.nonNull(request.getAddress())){
            profile.setAddress(request.getAddress());
        }

        if (Objects.nonNull(request.getBirthday())) {
            profile.setBirthday(new Date(request.getBirthday().getTime()));
        }

        account.setProfile(profile);
        profile.setAccount(account);

        accountRepository.save(account);

        return ResponseEntity
                .ok(profile);
    }


    @Override
    @Transactional
    public ResponseEntity updateAccount(AccountRequest request) {
        var accountOptional = accountRepository.findByUsername(request.getUsername());
        if(accountOptional.isEmpty()){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var account = accountOptional.get();
        var profile = account.getProfile();

        if(Objects.nonNull(request.getEnabled())){
            account.setEnabled(request.getEnabled());
        }

        if(StringUtils.hasText(request.getFirstname())){
            profile.setFirstname(request.getFirstname());
        }

        if(StringUtils.hasText(request.getLastname())){
            profile.setLastname(request.getLastname());
        }

        if(Objects.nonNull(request.getGender())){
            profile.setGender(request.getGender());
        }

        if(Objects.nonNull(request.getMobile())){
            profile.setMobile(request.getMobile());
        }

        if (Objects.nonNull(request.getBirthday())) {
            profile.setBirthday(new Date(request.getBirthday().getTime()));
        }

        if(Objects.nonNull(request.getAddress())){
            profile.setAddress(request.getAddress());
        }

        accountRepository.save(account);

        return ResponseEntity
                .ok(profile);
    }
}
