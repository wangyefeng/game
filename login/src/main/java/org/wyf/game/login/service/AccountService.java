package org.wyf.game.login.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wyf.game.common.GlobalConstant;
import org.wyf.game.common.RedisKeys.Locks;
import org.wyf.game.common.http.HttpResp;
import org.wyf.game.login.AccountType;
import org.wyf.game.login.entity.Account;
import org.wyf.game.login.entity.User;
import org.wyf.game.login.repository.AccountRepository;
import org.wyf.game.login.response.LoginResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedissonClient redissonClient;

    private final Algorithm playerTokenSecret;

    private final Map<String, Object> jwtHeader;

    public AccountService() {
        // 设置JWT加密密钥
        playerTokenSecret = Algorithm.HMAC256(GlobalConstant.PLAYER_TOKEN_SECRET_KEY);
        jwtHeader = new HashMap<>();
        jwtHeader.put("typ", "JWT");
        jwtHeader.put("alg", "HS256");
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public Optional<Account> find(String username) {
        return accountRepository.findById(username);
    }

    public HttpResp<?> register(String username, String password) {
        RLock rLock = redissonClient.getLock(Locks.ACCOUNT_LOCK_PREFIX + AccountType.INNER.toString().toLowerCase());
        try {
            rLock.lock();
            if (accountRepository.existsById(username)) {
                return HttpResp.fail(1, "用户名已存在");
            }
            Account account = new Account(username, passwordEncoder.encode(password), new User(AccountType.INNER, System.currentTimeMillis()));
            accountRepository.save(account);
            return HttpResp.SUCCESS;
        } finally {
            rLock.unlock();
        }
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
        String playerToken = token(id);
        return HttpResp.success(new LoginResponse(id, playerToken));
    }

    private String token(int playerId) {
        try {
            JWTCreator.Builder builder = JWT.create().withClaim("playerId", playerId);
            long t = System.currentTimeMillis();
            return builder
                    .withHeader(jwtHeader)
                    .withIssuedAt(new Date(t))
                    .withExpiresAt(new Date(t + GlobalConstant.PLAYER_TOKEN_EXPIRE_TIME))
                    .sign(playerTokenSecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
