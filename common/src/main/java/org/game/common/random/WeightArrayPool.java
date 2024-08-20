package org.game.common.random;

import org.game.common.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * 按权重随机的随机池，随机池不可变，底层是数组实现的。
 *
 * @param <E> 元素类型
 * @author 王叶峰
 * @date 2024-07-29
 */
public class WeightArrayPool<E> {

    /**
     * 随机元素池
     */
    private final EWeight<E>[] randomPool;

    /**
     * 总权重
     */
    private final int sumWeight;

    public WeightArrayPool(Collection<E> elements) {
        this(e -> ((IWeight) e).weight(), elements);
    }

    public WeightArrayPool(E... elements) {
        this(e -> ((IWeight) e).weight(), elements);
    }

    public WeightArrayPool(WeightCalculator<E> calculator, E... elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.length];
        int i = 0;
        int s = 0;
        for (E e : elements) {
            int weight = calculator.weight(e);
            if (weight > 0) {
                s += weight;
                randomPool[i++] = new EWeight<>(e, weight, s);
            }
        }
        if (s == 0) {
            throw new IllegalArgumentException("总权重值必须大于0！");
        }
        this.sumWeight = s;
    }

    public WeightArrayPool(WeightCalculator<E> calculator, Collection<E> elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.size()];
        int i = 0;
        int s = 0;
        for (E e : elements) {
            int weight = calculator.weight(e);
            if (weight > 0) {
                s += weight;
                randomPool[i++] = new EWeight<>(e, weight, s);
            }
        }
        if (s == 0) {
            throw new IllegalArgumentException("总权重值必须大于0！");
        }
        this.sumWeight = s;
    }

    private int randomIndex() {
        if (randomPool.length == 1) {
            return 0;
        }
        int randVal = RandomUtil.random(0, sumWeight - 1);
        return binarySearch(randVal);
    }

    /**
     * 二分查找小于等于k的最大值的索引
     *
     * @param k 目标值
     * @return 索引 当k大于所有元素的总权重时，返回-1
     */
    int binarySearch(int k) {
        if (k >= sumWeight) {
            return -1;
        }
        int mid, L = 0, R = randomPool.length - 1;
        int res = R + 1;
        while (L <= R) {
            mid = L + (R - L) / 2;
            if (randomPool[mid].getSumWeight() > k) {
                R = mid - 1;
                res = mid;
            } else if (randomPool[mid].getSumWeight() < k) {
                L = mid + 1;
            } else {
                res = mid + 1;
                break;
            }
        }
        return res;
    }

    /**
     * 随机出一组元素
     *
     * @return 随机一组元素
     */
    public E[] random(E[] result) {
        int count = result.length;
        Assert.isTrue(count > 0, "count必须大于0！");
        for (int i = 0; i < count; i++) {
            result[i] = random();
        }
        return result;
    }

    /**
     * 随机出一组元素
     */
    public void random(List<E> container, int count) {
        Assert.isTrue(count > 0, "count必须大于0！");
        for (int i = 0; i < count; i++) {
            container.add(random());
        }
    }

    /**
     * 随机元素
     *
     * @return 随机元素
     */
    public E random() {
        return randomPool[randomIndex()].getE();
    }


    /**
     * 按照给定的总权重随机出一个元素
     *
     * @param weight 总权重
     * @return 随机池的一个元素或者null
     */
    public E randomBySumWeight(int weight) {
        Assert.isTrue(weight >= sumWeight, "权重必须大于当前总权重！");
        int randVal = RandomUtil.random(0, weight - 1);
        int index = binarySearch(randVal);
        return index >= 0 ? randomPool[index].getE() : null;
    }
}
