package org.game.config.data.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "config_reward")
public class CfgReward implements Cfg<Integer> {

    @Id
    private Integer id;

    private List<SimpleItem> items;

    public CfgReward() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public List<SimpleItem> getItems() {
        return items;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setItems(List<SimpleItem> items) {
        this.items = items;
    }
}
