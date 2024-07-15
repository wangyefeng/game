package org.wangyefeng.game.logic.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "players")
public class Player {

    @Id
    private int id;

    private String name;

    private Bag bag;

    public Player() {
    }

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        bag = new Bag();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bag getBag() {
        return bag;
    }
}
