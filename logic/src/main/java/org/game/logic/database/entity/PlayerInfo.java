package org.game.logic.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PlayerInfo extends BaseInfo {

    private String name;

    private int level;

    private long coin;

    private LocalDate dailyResetDate;

    private PlayerInfo() {
        // for JPA
    }

    public PlayerInfo(int playerId, String name) {
        super(playerId);
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

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
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
}
