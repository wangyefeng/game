package org.game.logic.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 功能数据
 *
 * @author 王叶峰
 */
@Document
public class ActivityInfo extends Entity {

    private Set<Integer> activityIds; // 功能ID集合

    public ActivityInfo(int playerId) {
        super(playerId);
        this.activityIds = new HashSet<>();
    }

    public Set<Integer> getActivityIds() {
        return activityIds;
    }
}
