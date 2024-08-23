package org.game.common.random;

import org.game.common.util.Assert;

import java.util.Collection;

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

    public static <E extends IWeight> WeightArrayPool<E> createPool(Collection<E> elements) {
        return new WeightArrayPool<>(IWeight::weight, elements);
    }

    public static <E extends IWeight> WeightArrayPool<E> createPool(E[] elements) {
        return new WeightArrayPool<>(IWeight::weight, elements);
    }

    public WeightArrayPool(WeightCalculator<E> calculator, E[] elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.length];
        int s = 0;
        int i = 0;
        for (E e : elements) {
            int weight = calculator.weight(e);
            Assert.isTrue(weight > 0, "权重必须大于0！");
            s += weight;
            randomPool[i++] = new EWeight<>(e, s);
        }
    }

    public WeightArrayPool(WeightCalculator<E> calculator, Collection<E> elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.size()];
        int s = 0;
        int i = 0;
        for (E e : elements) {
            int weight = calculator.weight(e);
            Assert.isTrue(weight > 0, "权重必须大于0！");
            s += weight;
            randomPool[i++] = new EWeight<>(e, s);
        }
    }

    /**
     * 二分查找小于等于key的最大值的元素
     *
     * @param key 目标值
     * @return 随机池的一个元素或者null 当key大于所有元素的总权重时，返回null
     */
    private E binarySearch(int key) {
        int low = 0;
        int high = randomPool.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = randomPool[mid].sumWeight;

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return randomPool[mid].e;
            }
        }
        return randomPool[low].e;
    }

    /**
     * 随机元素
     *
     * @return 随机元素
     */
    public E random() {
        if (randomPool.length == 1) {
            return randomPool[0].e();
        }
        int randVal = RandomUtil.random(1, sumWeight());
        return binarySearch(randVal);
    }

    /**
     * 按照给定的总权重随机出一个元素
     *
     * @param weight 总权重
     * @return 随机池的一个元素或者null
     */
    public E randomByWeight(int weight) {
        Assert.isTrue(weight >= sumWeight(), "权重必须大于当前总权重！");
        int randVal = RandomUtil.random(1, weight);
        if (randVal > sumWeight()) {
            return null;
        }
        return binarySearch(randVal);
    }

    private int sumWeight() {
        return randomPool[randomPool.length - 1].sumWeight();
    }

    private record EWeight<E>(E e, int sumWeight) {
    }
}
