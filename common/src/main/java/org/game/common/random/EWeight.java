package org.game.common.random;

import org.game.common.util.Assert;

import java.util.Objects;

/**
 * 带权重的元素
 *
 * @param <E>    元素类型
 * @param e      元素
 * @param weight 权重
 * @author wangyefeng
 * @date 2014-07-29
 */
public record EWeight<E>(E e, int weight) implements IWeight {
    public EWeight(E e, int weight) {
        Assert.isTrue(weight > 0, "权重必须大于0");
        this.e = Objects.requireNonNull(e);
        this.weight = weight;
    }
}