package org.game.spring.cache.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 掉落表
 *
 * @author 王叶峰
 * @date 2021年7月30日
 */
@Entity
public class CfgActivity implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(name = "server_type", columnDefinition = "INT COMMENT '活动类型'")
    private int type;

    @Column(name = "`partition`", columnDefinition = "INT DEFAULT 1 COMMENT '1:活动 2：公告'")
    private int partition;

    @Column(name = "start_time", columnDefinition = "VARCHAR(64) COMMENT '活动开始时间'", nullable = false)
    private String startTime;

    @Column(name = "end_time", columnDefinition = "VARCHAR(64) COMMENT '活动结束时间'", nullable = false)
    private String endTime;

    @Column(name = "client_limit", columnDefinition = "VARCHAR(64) COMMENT '活动可见的最低版本号'", nullable = false)
    private String clientLimit;

    @Column(name = "channel", columnDefinition = "VARCHAR(64) COMMENT '活动生效的渠道'", nullable = false)
    private String channel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "item_id", columnDefinition = "json")
    private int[] itemId;

    @Column(name = "common_item_id", columnDefinition = "INT COMMENT '活动道具id'")
    private int commonItemId;

    @Column(name = "name_zh", columnDefinition = "VARCHAR(64) COMMENT '活动名'", nullable = false)
    private String name;

    @Column(name = "email_id", columnDefinition = "INT COMMENT '邮件模板id'")
    private int emailId;

    @Column(name = "rank_email_id", columnDefinition = "INT default 0  COMMENT '排行榜邮件模板id'")
    private int rankEmailId;

    @Column(name = "unlock_gun_level", columnDefinition = "INT COMMENT '活动解锁炮倍等级 -1就是没有限制'")
    private int unlockGunLevel;

    @Column(name = "unlock_vip", columnDefinition = "INT COMMENT '活动解锁VIP等级'")
    private int unlockVip;

    public CfgActivity() {
    }

    public int getUnlockGunLevel() {
        return unlockGunLevel;
    }

    public int getRankEmailId() {
        return rankEmailId;
    }

    public int getCommonItemId() {
        return commonItemId;
    }

    public int getUnlockVip() {
        return unlockVip;
    }

    public String getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getType() {
        return type;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getClientLimit() {
        return clientLimit;
    }

    public int[] getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public int getEmailId() {
        return emailId;
    }

    public int getPartition() {
        return partition;
    }

    public String getChannel() {
        return channel;
    }
}
