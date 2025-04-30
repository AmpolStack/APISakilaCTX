package com.sakila.sakila_project.infrastructure.filters;

import com.sakila.sakila_project.application.custom.AuthUser;
import com.sakila.sakila_project.application.usecases.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JwtSecurityFilter(JwtService jwtService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            ProcessToken(request);
        }
        catch (Exception e){
            log.error("Failed to process JWT: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
        logger.info("Processing complete. Return bach control to framework");

        filterChain.doFilter(request, response);
    }

    private void ProcessToken(HttpServletRequest request){

        String tokenHeader = request.getHeader("Authorization");

        if(tokenHeader.isEmpty()){
            log.error("Authorization header is empty");
            return;
        }

        if(!tokenHeader.startsWith("Bearer ")){
            log.error("the header is invalid");
            return;
        }

        final var token = tokenHeader.substring("Bearer ".length());

        if(!this.jwtService.isTokenValid(token)){
            log.error("the token is invalid");
            return;
        }

        log.info("PROCESSING TOKEN... {}", token);
        final var claims = this.jwtService.getAllClaims(token);
        var name = claims.getSubject();
        var id = claims.getId();
        var phone = claims.get("phone", String.class);
        var email = claims.get("email", String.class);

        log.info("PROCESSING STAFF...\nNAME: {} - ID: {}", name, id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null){
            log.info("ALREADY LOGGED IN: {}", name);
            return;
        }

        log.info("CREATE AUTHENTICATION INSTANCE FOR : {}", name);

        var user = new AuthUser();
        user.setId(Integer.parseInt(id));
        user.setUsername(name);
        user.setPassword(id);
        user.setEmail(email);
        user.setPhone(phone);

        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

}
