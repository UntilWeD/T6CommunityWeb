package com.teamsix.firstteamproject.global.config;

import com.teamsix.firstteamproject.user.service.JwtAuthenticationFilter;
import com.teamsix.firstteamproject.user.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
            // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
            .httpBasic(HttpBasicConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            // JWT를 사용하기 때문에 세션을 사용하지 않음
            .sessionManagement(configurer ->
                    configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize ->
                    authorize
                            // 해당 API에 대해서는 모든 요청을 허가
                            .requestMatchers("/user/signIn").permitAll()
                            .requestMatchers("/user/register").permitAll()
                            .requestMatchers("/user/email/*").permitAll()
                            .requestMatchers("/travelplan/*").permitAll()
                            .requestMatchers( "/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            // USER 권한이 있어야 요청할 수 있음
                            .requestMatchers("/user/test").hasRole("USER")
                            // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                            .anyRequest().authenticated()
                    )
            // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
