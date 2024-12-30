package org.game.login.controller;

import org.game.login.TokenUtils;
import org.game.login.entity.Account;
import org.game.login.response.LoginResponse;
import org.game.login.response.RegisterResponse;
import org.game.login.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/register")
    public ResponseEntity<RegisterResponse> register(String username, String password) {
        return ResponseEntity.ok(accountService.register(username, password));
    }

    @RequestMapping(value = "/login")
    public ResponseEntity<?> login(String username, String password) {
        Account account = accountService.login(username, password);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        int userId = account.getUser().getId();
        return new ResponseEntity<>(new LoginResponse(userId, TokenUtils.token(userId)), HttpStatus.OK);
    }

}
