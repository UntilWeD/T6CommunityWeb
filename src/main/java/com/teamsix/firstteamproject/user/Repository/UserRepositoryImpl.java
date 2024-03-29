package com.teamsix.firstteamproject.user.Repository;

import com.teamsix.firstteamproject.user.DTO.RegistryForm;
import com.teamsix.firstteamproject.user.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.net.URLDecoder;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryImpl(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public RegistryForm saveUser(RegistryForm registryForm) {
        log.info("[UserRepository] Executing the saveUser method ");

        String sql = "INSERT INTO user(id, email, pw, name) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                null, registryForm.getEmail(), registryForm.getPw(), registryForm.getName());

        log.info("[UserRepository] We Saved this User = {}", registryForm);

        return registryForm;
    }

    @Override
    public User updateUser(User user, Long id) {
        return null;
    }

    @Override
    public User deleteUser(User user) {
        return null;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";


        Optional<User> findUser =null;

        try{
            log.info("{}", URLDecoder.decode(email, "UTF-8"));
            findUser = Optional.of(jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email));
        } catch (Exception ex){
            log.info("[유저리포지토리] findUser를 찾는 도중 오류가 발생하였습니다.");
        }

        return findUser;
    }

    @Override
    public Optional<User> setEmailVerifiedById(Long userId) {
        log.info("[UserRepository] setEmailVerifiedById Method is Executing.. Id : {}", userId);

        String sql = "UPDATE user SET email_verification= true WHERE id = ? AND emailVerified = false";
        jdbcTemplate.update(sql, userId);

        log.info("Value is changed");

        sql = "SELECT * FROM user WHERE number = ?";
        Optional<User> findUser = null;

        try{
            findUser = Optional.of(jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), userId));
            return findUser;
        } catch (Exception e){
            log.info("[UserRepostiory] While finding findUser, Error appeared...");
            return Optional.empty();
        }
    }


}
