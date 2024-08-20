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
    private int sumWeight;

    public WeightArrayPool(List<? extends IWeight> elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.size()];
        for (int i = 0; i < randomPool.length; i++) {
            IWeight element = elements.get(i);
            int weight = element.weight();
            if (weight > 0) {
                sumWeight += weight;
                randomPool[i] = new EWeight(element, weight, sumWeight);
            }
        }
    }

    public WeightArrayPool(IWeight... elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.length];
        for (int i = 0; i < randomPool.length; i++) {
            IWeight element = elements[i];
            int weight = element.weight();
            if (weight > 0) {
                sumWeight += weight;
                randomPool[i] = new EWeight(element, weight, sumWeight);
            }
        }
    }

    public WeightArrayPool(WeightCalculator<E> calculator, List<E> elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.size()];
        for (int i = 0; i < randomPool.length; i++) {
            E element = elements.get(i);
            int weight = calculator.weight(element);
            if (weight > 0) {
                sumWeight += weight;
                randomPool[i] = new EWeight(element, weight, sumWeight);
            }
        }
    }

    public WeightArrayPool(WeightCalculator<E> calculator, E... elements) {
        Assert.notEmpty(elements, "随机池不能为空！");
        this.randomPool = new EWeight[elements.length];
        for (int i = 0; i < randomPool.length; i++) {
            E element = elements[i];
            int weight = calculator.weight(element);
            if (weight > 0) {
                sumWeight += weight;
                randomPool[i] = new EWeight(element, weight, sumWeight);
            }
        }
    }


    // 检查空池
    private void checkEmptyPool() {
        if (randomPool.length == 0) {
            throw new EmptyPoolException();
        }
    }

    private E randomOneNotCheck() {
        return randomPool[randomEWeightOne()].getE();
    }

    private int randomEWeightOne() {
        if (randomPool.length == 1) {
            return 0;
        }
        int randVal = RandomUtil.random(0, sumWeight - 1);
        return binarySearch(randVal);
    }

    private int binarySearch(int k) {
        int mid, L = 0, R = randomPool.length - 1;
        if (k >= randomPool[R].getSumWeight()) {
            return -1;
        }
        int res = R + 1; //也可以定义为 R，区别在于整个数组均比k小的返回值
        while (L <= R) {
            mid = L + (R - L) / 2; //避免溢出
            if (randomPool[mid].getSumWeight() > k) {
                R = mid - 1;
                res = mid;
            } else if (randomPool[mid].getSumWeight() < k) {
                L = mid + 1;
            } else {
                res = mid;
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
        checkEmptyPool();
        for (int i = 0; i < count; i++) {
            result[i] = randomOneNotCheck();
        }
        return result;
    }

    /**
     * 随机出一组元素
     */
    public void random(List<E> container, int count) {
        Assert.isTrue(count > 0, "count必须大于0！");
        checkEmptyPool();
        for (int i = 0; i < count; i++) {
            container.add(randomOneNotCheck());
        }
    }

    /**
     * 随机元素
     *
     * @return 随机元素
     */
    public E random() {
        checkEmptyPool();
        return randomOneNotCheck();
    }

    /**
     * 随机出一组不重复的元素
     *
     * @return 元素数组
     */
    public E[] randomUnique(E[] result) {
        checkEmptyPool();
        int resultLength = result.length;
        int poolLength = randomPool.length;
        Assert.isTrue(resultLength > 0, "count必须大于0！");
        Assert.isTrue(resultLength <= poolLength, "count必须小于等于随机池数量！");
        if (resultLength == poolLength) {
            for (int i = 0; i < resultLength; i++) {
                result[i] = randomPool[i].getE();
            }
        } else if (resultLength == 1) {
            result[0] = randomOneNotCheck();
            return result;
        } else {
            // 此处随机算法会破坏之前的数组顺序，需要重新计算数据的权重范围
            for (int i = 0; i < resultLength; i++) {
                int randVal = RandomUtil.random(0, sumWeight - 1);
                for (int j = 0; j < poolLength; j++) {
                    EWeight<E> eWeight = randomPool[j];
                    int weight = eWeight.weight();
                    if (randVal < weight) {
                        result[i] = eWeight.getE();
                        if (i < resultLength - 1) {
                            // 交换随机到的元素和最后的元素的位置，并减少总权重值
                            ArrayUtil.swap(randomPool, j, poolLength - 1 - i);
                            sumWeight -= eWeight.weight();
                        }
                        break;
                    }
                    randVal -= weight;
                }
            }
            // 重新计算数据的权重范围
            recalculate();
        }
        return result;
    }

    private void recalculate() {
        sumWeight = 0;
        int poolLength = randomPool.length;
        for (int i = 0; i < poolLength; i++) {
            EWeight<E> e = randomPool[i];
            sumWeight += e.weight();
            e.setSumWeight(sumWeight);
        }
    }

    /**
     * 随机出一组不重复的元素
     */
    public void randomUnique(Collection<E> container, int count) {
        checkEmptyPool();
        int poolLength = randomPool.length;
        Assert.isTrue(count > 0 && count <= poolLength, "count必须是小于或者到随机池数量的正整数！count=" + count);
        if (count == poolLength) {
            for (int i = 0; i < count; i++) {
                container.add(randomPool[i].getE());
            }
        } else if (count == 1) {
            container.add(randomOneNotCheck());
        } else {
            // 此处随机算法会破坏之前的数组顺序，需要重新计算数据的权重范围
            for (int i = 0; i < count; i++) {
                int randVal = RandomUtil.random(0, sumWeight - 1);
                for (int j = 0; j < poolLength; j++) {
                    EWeight<E> eWeight = randomPool[j];
                    int weight = eWeight.weight();
                    if (randVal < weight) {
                        container.add(eWeight.getE());
                        if (i < count - 1) {
                            // 交换随机到的元素和最后的元素的位置，并减少总权重值
                            ArrayUtil.swap(randomPool, j, poolLength - 1 - i);
                            sumWeight -= eWeight.weight();
                        }
                        break;
                    }
                    randVal -= weight;
                }
            }
            // 重新计算数据的权重范围
            recalculate();
        }
    }


    /**
     * 按照给定的总权重随机出一个元素
     *
     * @param weight 总权重
     * @return 随机池的一个元素或者null
     */
    public E randomBySumWeight(int weight) {
        Assert.isTrue(weight >= sumWeight, "权重必须大于当前总权重！");
        checkEmptyPool();
        int randVal = RandomUtil.random(0, weight - 1);
        int index = binarySearch(randVal);
        return index >= 0 ? randomPool[index].getE() : null;
    }
}
