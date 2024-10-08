package com.teamsix.firstteamproject.user.service.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;


/**
 * 클라이언트 요청으로부터 JWT 토큰을 처리하고 유효성을 검증하여 토큰의 인증 정보(Authentication)를
 * SecurityContext에 저장하여 인증된 요청을 처리할 수 있도록 한다.
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //1. Request Header에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try{
            //2. validateToken으로 토큰 유효성 검사
            if(token != null && jwtTokenProvider.validateToken(token)){
                //토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 필터에서 서비스를 갖고와서 예외를 던질시에 GlobalExceptionController로 가게되나?
        } catch (ExpiredJwtException ex){
            log.warn("Expired JWT token: {}", ex.getMessage());
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.getWriter().write("TOKEN_EXPIRED : The access token has expired. Please refresh your token.");
            return;
        }

        chain.doFilter(request, response);
    }

    //Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
