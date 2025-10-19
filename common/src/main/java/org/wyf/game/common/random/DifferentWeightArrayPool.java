package org.wyf.game.common.random;

import org.wyf.game.common.util.ArrayUtil;
import org.wyf.game.common.util.Assert;

import java.util.Collection;

/**
 * 按权重随机的随机池，随机池不可变，底层是数组实现的。
 *
 * @param <E> 元素类型
 * @author 王叶峰
 */
public class DifferentWeightArrayPool<E> {

    /**
     * 随机元素池
     */
    private final EWeight<E>[] randomPool;

    /**
     * 权重总和
     */
    private int sumWeight;

    public static <E extends IWeight> DifferentWeightArrayPool<E> createPool(Collection<E> elements) {
        return new DifferentWeightArrayPool<>(elements, IWeight::weight);
    }

    public static <E extends IWeight> DifferentWeightArrayPool<E> createPool(E[] elements) {
        return new DifferentWeightArrayPool<>(elements, IWeight::weight);
    }

    @SuppressWarnings("unchecked")
    public DifferentWeightArrayPool(E[] elements, WeightCalculator<E> calculator) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.length];
        int i = 0;
        for (E e : elements) {
            int weight = calculator.weight(e);
            Assert.isTrue(weight > 0, "权重必须大于0！");
            randomPool[i++] = new EWeight<>(e, weight);
            sumWeight += weight;
        }
    }

    @SuppressWarnings("unchecked")
    public DifferentWeightArrayPool(Collection<E> elements, WeightCalculator<E> calculator) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.size()];
        int i = 0;
        for (E e : elements) {
            int weight = calculator.weight(e);
            Assert.isTrue(weight > 0, "权重必须大于0！");
            randomPool[i++] = new EWeight<>(e, weight);
            sumWeight += weight;
        }
    }

    /**
     * 随机元素
     *
     * @return 随机元素
     */
    public synchronized E random() {
        return randomPool[randomIndex()].e;
    }

    /**
     * 随机元素
     *
     * @return 随机元素
     */
    private int randomIndex() {
        if (randomPool.length == 1) {
            return 0;
        }
        int randVal = RandomUtil.random(1, sumWeight);
        for (int i = 0; i < randomPool.length; i++) {
            EWeight<E> eWeight = randomPool[i];
            if (randVal <= eWeight.weight()) {
                return i;
            }
            randVal -= eWeight.weight();
        }
        throw new IllegalStateException("随机数值越界！");
    }

    /**
     * 随机不同的元素
     *
     * @return 随机不同的元素列表
     */
    public synchronized E[] randomDifferent(E[] result) {
        int size = result.length;
        Assert.isTrue(size <= randomPool.length, "结果数组长度不能大于随机池长度！");
        if (size == 0) {
            return result;
        }
        if (size == 1) {
            result[0] = random();
            return result;
        }
        if (size == randomPool.length) {
            for (int i = 0; i < size; i++) {
                result[i] = randomPool[i].e;
            }
            return result;
        }
        int oldSum = sumWeight;
        for (int i = 0; i < result.length; i++) {
            int last = randomPool.length - i - 1;
            int index =  randomIndex();
            result[i] = randomPool[index].e;
            sumWeight -= randomPool[index].weight;
            ArrayUtil.swap(randomPool, index, last);
        }
        sumWeight = oldSum;
        return result;
    }

    /**
     * 随机不同的元素
     */
    public synchronized void randomDifferent(int count, Collection<E> result) {
        Assert.isTrue(count <= randomPool.length, "结果数组长度不能大于随机池长度！");
        if (count == 0) {
            return;
        }
        if (count == 1) {
            result.add(random());
            return;
        }
        if (count == randomPool.length) {
            for (EWeight<E> eWeight : randomPool) {
                result.add(eWeight.e);
            }
            return;
        }
        int oldSum = sumWeight;
        for (int i = 0; i < count; i++) {
            int last = randomPool.length - i - 1;
            int index =  randomIndex();
            result.add(randomPool[index].e);
            sumWeight -= randomPool[index].weight;
            ArrayUtil.swap(randomPool, index, last);
        }
        sumWeight = oldSum;
    }

    private record EWeight<E>(E e, int weight) {
    }
}
