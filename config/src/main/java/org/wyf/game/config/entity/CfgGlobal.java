package org.wyf.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Entity
@Valid
public class CfgGlobal implements Cfg<String> {

    @Id
    @Column(name = "`key`", columnDefinition = "VARCHAR(50) COMMENT '全局配置项的键'")
    private String key;

    @NotBlank(message = "全局配置项的值不能为空")
    @Column(columnDefinition = "VARCHAR(200) COMMENT '全局配置项的值'")
    private String value;

    @Override
    public String getId() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
