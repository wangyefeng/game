package org.game.config.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.Valid;

@Entity
@Valid
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "none"))
public class CfgSeason extends CfgTimeIntervalFunction {

    private int level;

    public int getLevel() {
        return level;
    }
}
