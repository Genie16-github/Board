package com.example.board.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/posts", "/api/posts/**", "/h2-console/**").permitAll() // 누구나 접근
                        .anyRequest().authenticated() // 나머지 API는 인증 필요
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/") // 로그인 성공 시 이동 경로
                        .permitAll()            // 로그인 폼 모두 허용
                )
                .logout(LogoutConfigurer::permitAll            // 로그아웃 모두 허용
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // H2 콘솔 접근 허용
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
