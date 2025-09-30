package org.wyf.game.config.entity;

import jakarta.persistence.Entity;
import jakarta.validation.Valid;

@Entity
@Valid
public class CfgSeason extends CfgTimeIntervalFunction {

    private int level;

    public int getLevel() {
        return level;
    }
}
