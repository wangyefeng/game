package org.game.login.controller;

import org.game.common.http.HttpResp;
import org.game.login.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/register")
    public HttpResp<?> register(String username, String password) {
        return accountService.register(username, password);
    }

    @RequestMapping(value = "/login")
    public HttpResp<?> login(String username, String password) {
        return accountService.login(username, password);
    }

}
