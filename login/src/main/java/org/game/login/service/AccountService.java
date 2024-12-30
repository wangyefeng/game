package org.game.login.service;

import org.game.login.entity.Account;
import org.game.login.entity.User;
import org.game.login.repository.AccountRepository;
import org.game.login.response.RegisterResponse;
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

    public RegisterResponse register(String username, String password) {
        // 检查用户是否已存在（可以根据用户名或邮箱）
        if (accountRepository.existsById(username)) {
            return new RegisterResponse(false, "用户名已存在！！！");
        }
        // 创建新用户
        Account account = new Account(username, password, new User(System.currentTimeMillis()));
        // 保存到数据库
        accountRepository.save(account);
        return new RegisterResponse(true, null);
    }

    public Account login(String username, String password) {
        Optional<Account> optionalAccount = find(username);
        if (optionalAccount.isEmpty()) {
            return null;
        }
        Account account = optionalAccount.get();
        if (!account.getPassword().equals(password)) {
            return null;
        }
        return account;
    }
}
