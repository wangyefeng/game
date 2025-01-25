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

    public ActivityInfo(int id) {
        super(id);
        this.activityIds = new HashSet<>();
    }

    public ActivityInfo() {
    }

    public Set<Integer> getActivityIds() {
        return activityIds;
    }
}
