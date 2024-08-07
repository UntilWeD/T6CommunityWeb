package com.teamsix.firstteamproject.user.service;

import com.teamsix.firstteamproject.user.dto.LoginForm;
import com.teamsix.firstteamproject.user.dto.RegistryForm;
import com.teamsix.firstteamproject.user.entity.JwtToken;
import com.teamsix.firstteamproject.user.entity.User;
import com.teamsix.firstteamproject.user.exception.UserAlreadyExistsException;
import com.teamsix.firstteamproject.user.exception.UserEmailVerificationException;
import com.teamsix.firstteamproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService{
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegistryForm register(RegistryForm registryForm) {
        log.info("register user = {}", registryForm);

        //패스워드 인코딩화 이거 생성할때 자동으로 하자.
        registryForm.setPw(passwordEncoder.encode(registryForm.getPw()));
        if(userRepository.findUserByEmail(registryForm.getEmail()).isEmpty()){
            throw new UserAlreadyExistsException(registryForm.getEmail());
        }
        return userRepository.saveUser(registryForm);
    }


    @Override
    public JwtToken signIn(LoginForm loginForm) {

        // 이런식으로 검증해야 하나? 좀더 나은방식이 없을까?
        if(!userRepository.findEmailVerificationByEmail(loginForm.getEmail())){
            throw new UserEmailVerificationException(loginForm.getEmail());
        }


        //1. username + password를 기반으로 Authentication 객체 생성
        //이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginForm.email, loginForm.pw);

        //2. 실제 검증. authenticate() 메서드를 통해 요청된 User에 대한 검증 진행
        // authenticated메서드가 실행될 때 CustomUserDetailsService에서 만든 loadUserByUsername 메서드 실행
        // 만약 유저가 존재하지 않을시에 UsernameNotFoundException 발생
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;

    }

    @Override
    public void logout(User user) {


    }

    @Override
    public Optional<User> setEmailVerify(Long userId) {
        log.info("[UserService] setEmailVerify Method is Executing...");
        Optional<User> findUser = userRepository.setEmailVerifiedById(userId);

        if(findUser.isEmpty()){
            log.info("해당 유저는 존재하지 않습니다.");
            return null;
        }
        return findUser;
    }
}
