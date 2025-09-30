package org.wyf.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;

@Entity
public class CfgItem implements Cfg<Integer> {

    @Id
    @Column(columnDefinition = "INT COMMENT '唯一id'")
    private int id;

    @Min(0)
    @Column(name = "`type`", columnDefinition = "INT COMMENT '物品类型'")
    private int type;

    private String name;

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
