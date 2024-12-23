package org.game.config.data.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config_item")
@Valid
public class CfgItem implements Cfg<Integer> {

    @Id
    private int id;

    @Min(100)
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
