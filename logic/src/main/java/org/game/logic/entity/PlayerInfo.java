package org.game.logic.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "player_info")
public class PlayerInfo extends Entity {

    private String name;

    private int level;

    public PlayerInfo(int id, String name) {
        super(id);
        this.name = name;
        this.level = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public PlayerInfo clone() {
        return (PlayerInfo) super.clone();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
