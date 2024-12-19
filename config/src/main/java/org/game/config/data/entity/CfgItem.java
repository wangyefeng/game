package org.game.config.data.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config_item")
public class CfgItem implements Cfg<Integer> {

    @Id
    private int id;

    private int type;

    private String name;

    public CfgItem() {
    }

    public CfgItem(int id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
