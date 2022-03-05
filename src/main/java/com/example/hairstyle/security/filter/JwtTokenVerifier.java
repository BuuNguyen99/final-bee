package com.example.hairstyle.security.filter;

import com.example.hairstyle.config.AppProperties;
import com.example.hairstyle.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtTokenVerifier extends OncePerRequestFilter {
    private final AppProperties appProperties;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = parseJwtToken(httpServletRequest);
        try {
            if (StringUtils.hasText(jwtToken) && jwtService.validateToken(jwtToken)) {
                String username = jwtService.getUsernameFromJwtToken(jwtToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                List<Map<String, String>> authorities = jwtService.getAuthoritiesFromJwtToken(jwtToken);

                Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities
                        .stream()
                        .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                        .collect(Collectors.toSet());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        simpleGrantedAuthorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
            httpServletResponse.setHeader("Error", e.getMessage());
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), error);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String parseJwtToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(appProperties.getAuth().getTokenPrefix())) {
            return headerAuth.substring(appProperties.getAuth().getTokenPrefix().length());
        }
        return null;
    }
}
