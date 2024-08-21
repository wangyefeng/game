package org.game.common.random;

import org.game.common.util.Assert;

import java.util.Objects;

/**
 * 带权重的元素
 *
 * @param <E>    元素类型
 * @author wangyefeng
 * @date 2014-07-29
 */
public class EWeight<E> {

    private final E e;
    private int sumWeight;

    public EWeight(E e, int sumWeight) {
        Assert.isTrue(sumWeight > 0, "权重必须大于0");
        this.e = Objects.requireNonNull(e);
        this.sumWeight = sumWeight;
    }

    public E getE() {
        return e;
    }

    public int getSumWeight() {
        return sumWeight;
    }

    public void setSumWeight(int sumWeight) {
        this.sumWeight = sumWeight;
    }
}