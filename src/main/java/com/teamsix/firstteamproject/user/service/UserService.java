package com.teamsix.firstteamproject.user.service;

import com.teamsix.firstteamproject.user.dto.UserLoginDTO;
import com.teamsix.firstteamproject.user.dto.UserRegistryDTO;
import com.teamsix.firstteamproject.user.dto.UserDTO;
import com.teamsix.firstteamproject.user.dto.UserUpdateDTO;
import com.teamsix.firstteamproject.user.entity.JwtToken;
import com.teamsix.firstteamproject.user.entity.User;
import com.teamsix.firstteamproject.user.exception.UserAlreadyExistsException;
import com.teamsix.firstteamproject.user.repository.UserRepository;
import com.teamsix.firstteamproject.user.repository.UserRepositoryJDBC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService{
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepositoryJDBC userRepositoryJDBC;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public UserDTO register(UserDTO dto) {

        // 서비스 레이어에서 해당 인코딩도 비즈니스 로직이기에 적합하다.
        dto.encodingPw(passwordEncoder.encode(dto.getPw()));
        if(userRepository.findUserByEmail(dto.getEmail()) != null){
            throw new UserAlreadyExistsException(dto.getEmail());
        }
        return userRepository.save(dto.toEntity()).toDTO();
    }


    public JwtToken signIn(UserLoginDTO userLoginDTO) {

        //1. username + password를 기반으로 Authentication 객체 생성
        //이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDTO.email, userLoginDTO.pw);

        //2. 실제 검증. authenticate() 메서드를 통해 요청된 User에 대한 검증 진행
        // authenticated메서드가 실행될 때 CustomUserDetailsService에서 만든 loadUserByUsername 메서드 실행
        // 만약 유저가 존재하지 않을시에 UsernameNotFoundException 발생
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        jwtToken.setUserId(userRepositoryJDBC.findUserByEmail(userLoginDTO.email).get().getId());

        return jwtToken;
    }


    public Optional<User> setEmailVerify(Long userId) {
        Optional<User> findUser = userRepositoryJDBC.setEmailVerifiedById(userId);

        if(findUser.isEmpty()){
            log.info("해당 유저는 존재하지 않습니다.");
            return null;
        }
        return findUser;
    }

    public UserDTO findUserById(Long userId){
        return UserDTO.toDto(userRepositoryJDBC.findUserById(userId));
    }

    public UserDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        if(!userUpdateDTO.getPw().equals(userUpdateDTO.getConfirmationPw())){
            throw new RuntimeException("User pw and confirmationPw is not equal.");
        }
        userUpdateDTO.setPw(passwordEncoder.encode(userUpdateDTO.getPw()));
        return UserDTO.toDto(userRepositoryJDBC.updateUser(userId, userUpdateDTO));
    }

    public Long deleteUser(Long userId) {
        return userRepositoryJDBC.deleteUser(userId);
    }

    private boolean verifyPassword(String rawPw, String encodedPw){
        return passwordEncoder.matches(rawPw, encodedPw);
    }

}
