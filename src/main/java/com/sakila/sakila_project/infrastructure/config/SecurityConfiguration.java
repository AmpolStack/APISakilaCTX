package com.sakila.sakila_project.infrastructure.config;

import com.sakila.sakila_project.infrastructure.filters.JwtSecurityFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtSecurityFilter jwtSecurityFilter;

    @Autowired
    public SecurityConfiguration(JwtSecurityFilter jwtSecurityFilter) {
        this.jwtSecurityFilter = jwtSecurityFilter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain JwtAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/staff/**"),
                        new AntPathRequestMatcher("/**/auth/**")
                ))
                .authorizeHttpRequests(request ->{
                    request.requestMatchers("/staff/open/**").permitAll();
                    request.anyRequest().authenticated();
                })
                .exceptionHandling(ex ->{
                    ex.authenticationEntryPoint(customAuthenticationEntryPoint());
                    ex.accessDeniedHandler(customAccessDeniedHandler());
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain GeneralUnauthorizedFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .authorizeHttpRequests(request ->
                        request.anyRequest().permitAll())
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

   //TODO: it may be necessary to implement a custom password decoder
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        var defaultConfig = new CorsConfiguration();
        defaultConfig.setAllowedOriginPatterns(List.of("*"));
        defaultConfig.setAllowCredentials(true);
        defaultConfig.setAllowedHeaders(List.of("*"));

        var speConfig = new CorsConfiguration();
        speConfig.setAllowedOriginPatterns(List.of("http://localhost:5500"));
        speConfig.setAllowedHeaders(List.of("*"));
        speConfig.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/actors/**", speConfig);
        source.registerCorsConfiguration("/**", defaultConfig);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No authorized access");
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
    }

}
