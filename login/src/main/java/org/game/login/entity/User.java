package org.game.login.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.game.login.AccountType;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private long createTime;

    User() {
    }

    public User(AccountType accountType, long createTime) {
        this.accountType = accountType;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
