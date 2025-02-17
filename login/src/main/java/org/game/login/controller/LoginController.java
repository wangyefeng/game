package org.game.login.controller;

import org.game.common.http.HttpResp;
import org.game.login.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/register")
    public HttpResp<?> register(String username, String password) {
        log.info("注册账号 username:{}, password:{}", username, password);
        return accountService.register(username, password);
    }

    @RequestMapping(value = "/login")
    public HttpResp<?> login(String username, String password) {
        log.info("登录账号 username:{}, password:{}  111", username, password);
        return accountService.login(username, password);
    }

}
