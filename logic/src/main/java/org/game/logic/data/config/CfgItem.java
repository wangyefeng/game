package org.game.logic.data.config;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config_item")
public class CfgItem implements Cfg<Integer> {

    @Id
    private int id;

    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
