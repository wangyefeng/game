package org.game.logic.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "player_info")
public class PlayerInfo extends Entity {

    @Id
    private int id;

    private String name;

    public PlayerInfo(int id, String name) {
        super(id);
        this.name = name;
    }

    public int getId() {
        return id;
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
}
