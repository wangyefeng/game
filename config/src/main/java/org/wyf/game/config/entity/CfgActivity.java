package org.wyf.game.config.entity;

import jakarta.persistence.Entity;
import jakarta.validation.Valid;

@Entity
@Valid
public class CfgActivity extends CfgTimeIntervalFunction {

    private String name;

    public String getName() {
        return name;
    }
}
