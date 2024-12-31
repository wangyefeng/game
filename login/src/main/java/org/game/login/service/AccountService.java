package org.game.login.service;

import org.game.common.util.TokenUtil;
import org.game.login.AccountType;
import org.game.login.entity.Account;
import org.game.login.entity.User;
import org.game.login.repository.AccountRepository;
import org.game.login.response.HttpResp;
import org.game.login.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public void save(Account account) {
        accountRepository.save(account);
    }

    public Optional<Account> find(String username) {
        return accountRepository.findById(username);
    }

    public HttpResp<?> register(String username, String password) {
        if (accountRepository.existsById(username)) {
            return HttpResp.fail(1, "用户名已存在");
        }
        Account account = new Account(username, password, new User(AccountType.INNER, System.currentTimeMillis()));
        accountRepository.save(account);
        return HttpResp.SUCCESS;
    }

    public HttpResp<LoginResponse> login(String username, String password) {
        Optional<Account> optionalAccount = find(username);
        if (optionalAccount.isEmpty()) {
            return HttpResp.fail(1, "用户不存在");
        }
        Account account = optionalAccount.get();
        if (!account.getPassword().equals(password)) {
            return HttpResp.fail(2, "密码错误");
        }
        int id = account.getUser().getId();
        return HttpResp.success(new LoginResponse(id, TokenUtil.token(id)));
    }
}
