package org.game.logic.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;

@jakarta.persistence.Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PlayerInfo extends Entity {

    @Id
    @Column(nullable = false)
    private int playerId;

    private String name;

    private int level;

    private int coin;

    private LocalDate dailyResetDate;

    private PlayerInfo() {
        // for JPA
    }

    public PlayerInfo(int playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        this.level = 1;
        this.dailyResetDate = LocalDate.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public PlayerInfo clone() throws CloneNotSupportedException {
        return (PlayerInfo) super.clone();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        if (coin < 0) {
            throw new IllegalArgumentException("金币不能为负数。");
        }
        this.coin = coin;
    }

    public LocalDate getDailyResetDate() {
        return dailyResetDate;
    }

    public void setDailyResetDate(LocalDate dailyResetDate) {
        this.dailyResetDate = dailyResetDate;
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }
}
