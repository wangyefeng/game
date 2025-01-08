package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Entity
@Valid
public class CfgItem implements Cfg<Integer> {

    @Id
    @Column(name = "`id`", columnDefinition = "INT COMMENT '唯一id'")
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
