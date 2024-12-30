package org.game.login.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long createTime;

    User() {
    }

    public User(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }
}
