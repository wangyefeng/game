package org.game.login.service;

import org.game.common.RedisKeys;
import org.game.common.http.HttpResp;
import org.game.common.util.TokenUtil;
import org.game.login.AccountType;
import org.game.login.entity.Account;
import org.game.login.entity.User;
import org.game.login.repository.AccountRepository;
import org.game.login.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.timer.Timer;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
        Account account = new Account(username, passwordEncoder.encode(password), new User(AccountType.INNER, System.currentTimeMillis()));
        accountRepository.save(account);
        return HttpResp.SUCCESS;
    }

    public HttpResp<LoginResponse> login(String username, String password) {
        Optional<Account> optionalAccount = find(username);
        if (optionalAccount.isEmpty()) {
            return HttpResp.fail(1, "用户不存在");
        }
        Account account = optionalAccount.get();
        if (!passwordEncoder.matches(password, account.getPassword())) {
            return HttpResp.fail(2, "密码错误");
        }
        int id = account.getUser().getId();
        String playerToken = TokenUtil.token(id, TokenUtil.PLAYER_TOKEN_SECRET, new Date(System.currentTimeMillis() + Timer.ONE_DAY * 30));
        redisTemplate.opsForValue().set(RedisKeys.PLAYER_TOKEN_PREFIX + id, playerToken, 30, TimeUnit.DAYS);
        return HttpResp.success(new LoginResponse(id, playerToken));
    }
}
