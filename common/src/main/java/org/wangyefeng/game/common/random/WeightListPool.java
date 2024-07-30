package org.wangyefeng.game.common.random;

import org.wangyefeng.game.common.util.ArrayUtil;
import org.wangyefeng.game.common.util.Assert;

import java.util.*;

/**
 * 权重随机类
 *
 * @param <E> 元素类型
 * @author 王叶峰
 * @date 2024-07-27
 */
public class WeightListPool<E> {

    /**
     * 随机池
     */
    private final List<EWeight<E>> randomPool = new ArrayList<>();

    /**
     * 总权重
     */
    private int sumWeight;

    /**
     * 添加元素到随机池
     *
     * @param e      元素
     * @param weight 权重
     */
    public void addPool(E e, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("权重不能为负数");
        }
        if (weight > 0) {
            randomPool.add(new EWeight(e, weight));
            sumWeight += weight;
        }
    }

    // 检查空池
    private void checkEmptyPool() {
        if (randomPool.isEmpty()) {
            throw new RandomPoolNotEnoughException("随机池为空");
        }
    }

    private E randomOneNotCheck() {
        return randomEWeightOne().e();
    }

    private EWeight<E> randomEWeightOne() {
        if (randomPool.size() == 1) {
            return randomPool.get(0);
        }
        int randVal = RandomUtil.random(0, sumWeight - 1);
        for (EWeight eWeight : randomPool) {
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
        int length = result.length;
        checkEmptyPool();
        Assert.isTrue(length > 0, "count必须大于0！");
        Assert.isTrue(length <= randomPool.size(), "count必须小于等于随机池数量！");
        if (length == randomPool.size()) {
            for (int i = 0; i < length; i++) {
                result[i] = randomPool.get(i).e();
            }
        } else {
            for (int i = 0; i < length; i++) {
                int last = randomPool.size() - i - 1;
                int index = RandomUtil.random(0, last);
                ArrayUtil.swap(randomPool, index, last);
                result[i] = randomPool.get(last).e();
            }
        }
        return result;
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
        for (EWeight<E> eWeight : randomPool) {
            if (randVal < eWeight.weight()) {
                return eWeight.e();
            }
            randVal -= eWeight.weight();
        }
        return null;
    }

    /**
     * 清空随机池
     */
    public void clearPool() {
        randomPool.clear();
        sumWeight = 0;
    }

    /**
     * 删除随机池的元素e
     *
     * @param e 元素
     * @return 是否成功
     */
    public boolean removePool(E e) {
        Iterator<EWeight<E>> iterator = randomPool.iterator();
        while (iterator.hasNext()) {
            EWeight eWeight = iterator.next();
            if (eWeight.e().equals(e)) {
                iterator.remove();
                sumWeight -= eWeight.weight();
                return true;
            }
        }
        return false;
    }

    /**
     * 空池判断
     *
     * @return true：空池；false：非空池
     */
    public boolean poolIsEmpty() {
        return randomPool.isEmpty();
    }

}
