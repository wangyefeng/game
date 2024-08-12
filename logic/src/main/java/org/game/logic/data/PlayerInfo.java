package org.game.logic.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "player_info")
public class PlayerInfo {

    @Id
    private int id;

    private String name;

    private List<Item> items;

    public PlayerInfo(int id, String name, List<Item> items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
