package org.game.logic.data.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.game.logic.data.config.Cfg;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import javax.management.timer.Timer;
import java.util.List;
import java.util.Objects;

/**
 * 鱼阵配置
 * 
 * @author 王叶峰
 * @date 2021年7月22日
 *
 */
@Entity
public class CfgFisheries implements Cfg<String> {

	@Id
	@Column(columnDefinition = "VARCHAR(30)")
	private String id;

	@Column(name = "direction", columnDefinition = "INT UNSIGNED COMMENT '方向'")
	private Integer direction;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "fishes", columnDefinition = "JSON comment '鱼列表'")
	private  List<FishInfo> fishes;

	CfgFisheries() {
	}

	public String getId() {
		return id;
	}

	public Integer getDirection() {
		return direction;
	}
	
	public List<FishInfo> getFishes() {
		return fishes;
	}
	
	public static class FishInfo {
		private int fishTempId;
		private double endTime;
		public int getEndTime() {
			return (int)(endTime * Timer.ONE_SECOND);
		}
		public int getFishTempId() {
			return fishTempId;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FishInfo) {
				FishInfo o = (FishInfo)obj;
				return this.fishTempId == o.fishTempId && this.endTime == o.endTime;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(fishTempId, endTime);
		}
	}
}
