package org.game.common.random;

import org.game.common.util.ArrayUtil;
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
    private final int totalWeight;

    public WeightArrayPool(List<? extends IWeight> elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.size()];
        int totalWeight = 0;
        for (int i = 0; i < randomPool.length; i++) {
            IWeight element = elements.get(i);
            randomPool[i] = new EWeight(element, element.weight());
            totalWeight += element.weight();
        }
        this.totalWeight = totalWeight;
    }

    public WeightArrayPool(IWeight... elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.length];
        int totalWeight = 0;
        for (int i = 0; i < randomPool.length; i++) {
            IWeight element = elements[i];
            randomPool[i] = new EWeight(element, element.weight());
            totalWeight += element.weight();
        }
        this.totalWeight = totalWeight;
    }

    public WeightArrayPool(WeightCalculator<E> calculator, List<E> elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.size()];
        int totalWeight = 0;
        for (int i = 0; i < randomPool.length; i++) {
            E element = elements.get(i);
            int weight = calculator.weight(element);
            randomPool[i] = new EWeight(element, weight);
            totalWeight += weight;
        }
        this.totalWeight = totalWeight;
    }

    public WeightArrayPool(WeightCalculator<E> calculator, E... elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.length];
        int totalWeight = 0;
        for (int i = 0; i < randomPool.length; i++) {
            E element = elements[i];
            int weight = calculator.weight(element);
            randomPool[i] = new EWeight(element, weight);
            totalWeight += weight;
        }
        this.totalWeight = totalWeight;
    }


    // 检查空池
    private void checkEmptyPool() {
        if (randomPool.length == 0) {
            throw new EmptyPoolException();
        }
    }

    private E randomOneNotCheck() {
        return randomEWeightOne().e();
    }

    private EWeight<E> randomEWeightOne() {
        if (randomPool.length == 1) {
            return randomPool[0];
        }
        int randVal = RandomUtil.random(0, totalWeight - 1);
        for (EWeight<E> eWeight : randomPool) {
            int weight = eWeight.weight();
            if (randVal < weight) {
                return eWeight;
            }
            randVal -= weight;
        }
        throw new RuntimeException("随机逻辑出错！");
    }

    /**
     * 随机出一组元素
     *
     * @return 随机一组元素
     */
    public E[] randomArray(E[] result) {
        int count = result.length;
        Assert.isTrue(count > 0, "count必须大于0！");
        checkEmptyPool();
        for (int i = 0; i < count; i++) {
            result[i] = randomOneNotCheck();
        }
        return result;
    }

    /**
     * 随机出一组不重复的元素
     *
     * @return 元素数组
     */
    public E[] randomUniqueArray(E[] result) {
        checkEmptyPool();
        Assert.isTrue(result.length > 0, "count必须大于0！");
        Assert.isTrue(result.length <= randomPool.length, "count必须小于等于随机池数量！");
        if (result.length == randomPool.length) {
            for (int i = 0; i < result.length; i++) {
                result[i] = randomPool[i].e();
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                int last = randomPool.length - i - 1;
                int index = RandomUtil.random(0, last);
                ArrayUtil.swap(randomPool, index, last);
                result[i] = randomPool[last].e();
            }
        }
        return result;
    }

    /**
     * 随机出一组不重复的元素
     *
     * @return 元素数组
     */
    public void randomUniqueList(int count, Collection<E> container) {
        checkEmptyPool();
        Assert.isTrue(count > 0 && count <= randomPool.length, "count必须是小于或者到随机池数量的正整数！count=" + count);
        if (count == randomPool.length) {
            for (int i = 0; i < count; i++) {
                container.add(randomPool[i].e());
            }
        } else {
            for (int i = 0; i < count; i++) {
                int last = randomPool.length - i - 1;
                int index = RandomUtil.random(0, last);
                ArrayUtil.swap(randomPool, index, last);
                container.add(randomPool[last].e());
            }
        }
    }


    /**
     * 按照给定的总权重随机出一个元素
     *
     * @param weight 总权重
     * @return 随机池的一个元素或者null
     */
    public E randomBySumWeight(int weight) {
        Assert.isTrue(weight >= totalWeight, "权重必须大于当前总权重！");
        checkEmptyPool();
        int randVal = RandomUtil.random(0, weight - 1);
        for (EWeight<E> eWeight : randomPool) {
            if (randVal < eWeight.weight()) {
                return eWeight.e();
            }
            randVal -= eWeight.weight();
        }
        return null;
    }
}
