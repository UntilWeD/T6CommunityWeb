package com.teamsix.firstteamproject.user.Controller;

import com.teamsix.firstteamproject.user.DTO.LoginForm;
import com.teamsix.firstteamproject.user.DTO.RegistryForm;
import com.teamsix.firstteamproject.user.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public interface UserController {
    public ResponseEntity<RegistryForm> register(RegistryForm registryForm, BindingResult bindingResult, Model model);
    public String login(LoginForm loginForm, BindingResult bindingResult);

}
