package org.game.config.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Valid
@Inheritance(strategy = InheritanceType.JOINED)
public class CfgFunction implements Cfg<Integer> {

    @Id
    @Column(name = "`id`", columnDefinition = "INT COMMENT '唯一id'")
    private int id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modules", columnDefinition = "JSON COMMENT '功能模块集合'")
    @Access(AccessType.PROPERTY)
    private int[] modules;

    @Transient
    private ModuleEnum[] moduleEnums;

    @Override
    public Integer getId() {
        return id;
    }

    public void setModules(int[] modules) {
        this.modules = modules;
        moduleEnums = new ModuleEnum[modules.length];
        for (int i = 0; i < modules.length; i++) {
            moduleEnums[i] = ModuleEnum.values()[modules[i]];
        }
    }

    public int[] getModules() {
        return modules;
    }

    public ModuleEnum[] getModuleEnums() {
        return moduleEnums;
    }
}
