package org.wangyefeng.game.common.random;

import org.wangyefeng.game.common.util.Assert;

import java.util.Objects;

/**
 * 带权重的元素
 *
 * @param <E> 元素类型
 * @author wangyefeng
 * @date 2014-07-29
 */
public class EWeight<E> implements IWeight {
    // 元素
    final E e;
    // 权重
    final int weight;

    public EWeight(E e, int weight) {
        Assert.isTrue(weight > 0, "权重必须大于0");
        this.e = Objects.requireNonNull(e);
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}