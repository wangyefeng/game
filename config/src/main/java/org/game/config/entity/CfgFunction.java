package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.Valid;

@Entity
@Valid
@Inheritance(strategy = InheritanceType.JOINED)
public class CfgFunction implements Cfg<Integer> {

    @Id
    @Column(name = "`id`", columnDefinition = "INT COMMENT '唯一id'")
    private int id;

    @Column(name = "`type`", columnDefinition = "INT COMMENT '类型'")
    private int type;

    @Override
    public Integer getId() {
        return id;
    }

    public int getType() {
        return type;
    }
}
