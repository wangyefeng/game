package org.game.common.random;

import org.game.common.util.Assert;
import org.game.common.util.ListUtil;

import java.util.ArrayList;
import java.util.Collection;
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
            randomPool.add(new EWeight(e, weight, sumWeight));
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
        if (k >= randomPool.get(R).getSumWeight()) {
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
     * 随机出一组不重复的元素
     *
     * @return 元素数组
     */
    public E[] randomUnique(E[] result) {
        checkEmptyPool();
        int length = result.length;
        int poolLength = randomPool.size();
        Assert.isTrue(length > 0, "count必须大于0！");
        Assert.isTrue(length <= randomPool.size(), "count必须小于等于随机池数量！");
        if (length == randomPool.size()) {
            for (int i = 0; i < length; i++) {
                result[i] = randomPool.get(i).getE();
            }
        } else if (length == 1) {
            result[0] = randomOneNotCheck();
            return result;
        } else {
            // 此处随机算法会破坏之前的数组顺序，需要重新计算数据的权重范围
            for (int i = 0; i < length; i++) {
                int randVal = RandomUtil.random(0, sumWeight - 1);
                for (int j = 0; j < poolLength; j++) {
                    EWeight<E> eWeight = randomPool.get(j);
                    int weight = eWeight.weight();
                    if (randVal < weight) {
                        result[i] = eWeight.getE();
                        if (i < length - 1) {
                            // 交换随机到的元素和最后的元素的位置，并减少总权重值
                            ListUtil.swap(randomPool, j, poolLength - 1 - i);
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

    /**
     * 随机出一组不重复的元素
     */
    public void randomUnique(Collection<E> container, int count) {
        checkEmptyPool();
        int poolLength = randomPool.size();
        Assert.isTrue(count > 0 && count <= poolLength, "count必须是小于或者到随机池数量的正整数！count=" + count);
        if (count == poolLength) {
            for (int i = 0; i < count; i++) {
                container.add(randomPool.get(i).getE());
            }
        } else if (count == 1) {
            container.add(randomOneNotCheck());
        } else {
            // 此处随机算法会破坏之前的数组顺序，需要重新计算数据的权重范围
            for (int i = 0; i < poolLength; i++) {
                int randVal = RandomUtil.random(0, sumWeight - 1);
                for (int j = 0; j < poolLength; j++) {
                    EWeight<E> eWeight = randomPool.get(j);
                    int weight = eWeight.weight();
                    if (randVal < weight) {
                        container.add(eWeight.getE());
                        if (i < poolLength - 1) {
                            // 交换随机到的元素和最后的元素的位置，并减少总权重值
                            ListUtil.swap(randomPool, j, poolLength - 1 - i);
                            sumWeight -= eWeight.weight();
                        }
                        break;
                    }
                    randVal -= weight;
                }
            }
            recalculate();
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
