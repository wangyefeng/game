package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import org.game.common.util.JsonUtil;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Valid
public class CfgTask implements Cfg<Integer> {

    @Id
    private int id;

    @Column(name = "function_id", columnDefinition = "INT UNSIGNED COMMENT '所属功能ID'")
    private int functionId;

    @Column(name = "`type`", columnDefinition = "INT UNSIGNED COMMENT '任务类型'")
    private int type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "args", columnDefinition = "JSON COMMENT '任务参数'")
    private String[] args;

    @Column(name = "target", columnDefinition = "BIGINT UNSIGNED COMMENT '任务目标'")
    private long target;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rewards", columnDefinition = "JSON COMMENT '奖励'")
    private List<SimpleItem> rewards;

    @Override
    public Integer getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String[] getArgs() {
        return args;
    }

    public long getTarget() {
        return target;
    }

    public List<SimpleItem> getRewards() {
        return rewards;
    }

    public int getFunctionId() {
        return functionId;
    }

    public static void main(String[] args) {
        System.out.println( JsonUtil.toJson(new SimpleItem(1, 100)));
    }
}
