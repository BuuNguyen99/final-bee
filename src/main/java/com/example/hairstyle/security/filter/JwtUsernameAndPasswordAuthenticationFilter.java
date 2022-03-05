package com.example.hairstyle.security.filter;

import com.example.hairstyle.config.AppProperties;
import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.payload.request.LoginRequest;
import com.example.hairstyle.payload.response.AuthResponse;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.security.jwt.JwtService;
import com.example.hairstyle.security.service.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final AppProperties appProperties;

    private final JwtService jwtService;

    @Autowired
    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                      AppProperties appProperties,
                                                      JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.appProperties = appProperties;
        this.jwtService = jwtService;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (BadCredentialsException e) {
            log.error(e.getMessage());
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(400);
                new ObjectMapper().writeValue(response.getOutputStream(), new MessageResponse(false, ResponseText.INCORRECT_USERNAME_PASSWORD_ERROR));
            } catch (IOException ioException) {
                log.error(e.getMessage());
            }
            throw new AccountExpiredException(ResponseText.INCORRECT_USERNAME_PASSWORD_ERROR);
        } catch (LockedException e) {
            log.error(e.getMessage());
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(400);
                new ObjectMapper().writeValue(response.getOutputStream(), new MessageResponse(false, ResponseText.INCORRECT_USERNAME_PASSWORD_ERROR));
            } catch (IOException ioException) {
                log.error(e.getMessage());
            }
        } catch (DisabledException e) {
            log.error(e.getMessage());
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(400);
                new ObjectMapper().writeValue(response.getOutputStream(), new MessageResponse(false, ResponseText.DISABLED_ACCOUNT));
            } catch (IOException ioException) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        UserDetailsImpl user = (UserDetailsImpl) authResult.getPrincipal();

        String accessToken = jwtService.generateJwtAccessToken(user);

        String refreshToken = jwtService.generateJwtRefreshToken(user);

        response.addHeader(HttpHeaders.AUTHORIZATION, appProperties.getAuth().getTokenPrefix() + accessToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), new AuthResponse(accessToken, refreshToken));
    }
}
