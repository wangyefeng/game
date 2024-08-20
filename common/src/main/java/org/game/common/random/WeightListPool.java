package org.game.common.random;

import org.game.common.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 可变长的按权重随机的随机池，底层数据是ArrayList
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
            sumWeight += weight;
            randomPool.add(new EWeight<>(e, weight, sumWeight));
        }
    }

    // 检查空池
    private void checkEmptyPool() {
        if (randomPool.isEmpty()) {
            throw new EmptyPoolException();
        }
    }

    private E randomOneNotCheck() {
        return randomPool.get(randomEWeightOne()).getE();
    }

    private int randomEWeightOne() {
        if (randomPool.size() == 1) {
            return 0;
        }
        int randVal = RandomUtil.random(0, sumWeight - 1);
        return binarySearch(randVal);
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

    private int binarySearch(int k) {
        int mid, L = 0, R = randomPool.size() - 1;
        if (k >= sumWeight) {
            return -1;
        }
        int res = R + 1;
        while (L <= R) {
            mid = L + (R - L) / 2; //避免溢出
            if (randomPool.get(mid).getSumWeight() > k) {
                R = mid - 1;
                res = mid;
            } else if (randomPool.get(mid).getSumWeight() < k) {
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

    // 重新计算权重
    private void recalculate() {
        sumWeight = 0;
        for (int i = 0; i < randomPool.size(); i++) {
            EWeight<E> e = randomPool.get(i);
            sumWeight += e.weight();
            e.setSumWeight(sumWeight);
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
        Assert.isTrue(weight >= sumWeight, "权重必须大于当前总权重！");
        checkEmptyPool();
        int index = binarySearch(randVal);
        return index >= 0 ? randomPool.get(index).getE() : null;
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
        boolean isRemoved = false;
        Iterator<EWeight<E>> iterator = randomPool.iterator();
        while (iterator.hasNext()) {
            EWeight eWeight = iterator.next();
            if (eWeight.getE().equals(e)) {
                iterator.remove();
                isRemoved = true;
                break;
            }
        }
        if (isRemoved) {
            recalculate();
        }
        return isRemoved;
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
