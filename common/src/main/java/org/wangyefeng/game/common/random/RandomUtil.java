package org.wangyefeng.game.common.random;


import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 伪随机工具类
 */
public class RandomUtil {

    private RandomUtil() {
    }

    /**
     * 伪随机两个值之间的数
     *
     * @param bound1 边界值1
     * @param bound2 边界值2
     * @return [bound1, bound2]的某个数
     * @throws IllegalArgumentException 当bound1 == bound2时
     */
    public static int random(int bound1, int bound2) {
        Assert.isTrue(bound2 >= bound1, "非法随机参数：bound1：" + bound1 + " bound2:" + bound2);
        if (bound2 == bound1) {
            return bound1;
        }
        int bound = Math.abs(bound1 - bound2) + 1;
        int num = ThreadLocalRandom.current().nextInt(bound);
        return bound1 + num;
    }

    /**
     * 伪随机两个值之间的数
     *
     * @param bound1 边界值1
     * @param bound2 边界值2
     * @return [bound1, bound2]的某个数
     * @throws IllegalArgumentException 当bound1 == bound2时
     */
    public static long random(long bound1, long bound2) {
        Assert.isTrue(bound2 >= bound1, "非法随机参数：bound1：" + bound1 + " bound2:" + bound2);
        if (bound2 == bound1) {
            return bound1;
        }
        long bound = Math.abs(bound1 - bound2) + 1;
        long num = ThreadLocalRandom.current().nextLong(bound);
        return bound1 + num;
    }

    /**
     * 伪随机两个值之间的数
     *
     * @param bound1 边界值1
     * @param bound2 边界值2
     * @return [bound1, bound2)的某个数
     */
    public static double random(double bound1, double bound2) {
        double samll = Math.min(bound1, bound2);
        double large = Math.max(bound1, bound2);
        double r = RandomUtil.nextDouble();
        return r * (large - samll) + samll;
    }

    /**
     * 伪随机出集合中某个元素
     *
     * @param c 随机库
     * @return 返回库中的某个元素
     * @throws NullPointerException     当c为null时
     * @throws IllegalArgumentException 当c没有元素时
     */
    public static <T> T random(Collection<T> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("随机库元素数量不能为0");
        }
        int size = c.size();
        @SuppressWarnings("unchecked")
        T[] ts = (T[]) new Object[size];
        c.toArray(ts);
        int index = random(0, size - 1);
        return ts[index];
    }

    /**
     * 伪随机出数组中某个元素
     *
     * @param a 随机数组
     * @return 返回数组中的某个对象
     * @throws NullPointerException     当a为null时
     * @throws IllegalArgumentException 当a长度为0时
     */
    public static <T> T random(T[] a) {
        int length = a.length;
        if (length == 0) {
            throw new IllegalArgumentException("随机数组长度必须大于0");
        }
        int index = random(0, length - 1);
        return a[index];
    }

    /**
     * 伪随机出数组中某个元素
     *
     * @param a 随机数组
     * @return 返回数组中的某个对象
     * @throws NullPointerException     当a为null时
     * @throws IllegalArgumentException 当a长度为0时
     */
    public static int random(int[] a) {
        int length = a.length;
        if (length == 0) {
            throw new IllegalArgumentException("随机数组长度必须大于0");
        }
        int index = random(0, length - 1);
        return a[index];
    }

    /**
     * 伪随机出数组中某个元素
     *
     * @param a 随机数组
     * @return 返回数组中的某个对象
     * @throws NullPointerException     当a为null时
     * @throws IllegalArgumentException 当a长度为0时
     */
    public static char random(char[] a) {
        int length = a.length;
        if (length == 0) {
            throw new IllegalArgumentException("随机数组长度必须大于0");
        }
        int index = random(0, length - 1);
        return a[index];
    }

    /**
     * 伪随机带权重的集合
     *
     * @param c                随机库集合
     * @param weightCalculator 权重计算器
     * @throws IllegalArgumentException 当c中元素通过权重计算器得到的权重为负数时
     */
    public static <T> T randomByWeight(WeightCalculator<T> weightCalculator, Collection<T> c) {
        int sum = 0;
        for (T t : c) {
            int weight = weightCalculator.weight(t);
            if (weight < 0) {
                throw new IllegalArgumentException("权重不能为负数：" + weight);
            }
            sum += weight;
        }
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : c) {
            int weight = weightCalculator.weight(t);
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        throw new RuntimeException();
    }

    /**
     * 伪随机权重加参数的集合
     *
     * @param c                随机库集合
     * @param weightCalculator 权重计算器
     * @throws IllegalArgumentException 当c中元素通过权重计算器得到的权重为负数时
     */
    public static <T> T randomByWeightAddParam(WeightCalculator<T> weightCalculator, Collection<T> c, int param) {
        int sum = 0;
        for (T t : c) {
            int weight = weightCalculator.weight(t) + param;
            if (weight < 0) {
                throw new IllegalArgumentException("权重不能为负数：" + weight);
            }
            sum += weight;
        }
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : c) {
            int weight = weightCalculator.weight(t) + param;
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        throw new RuntimeException();
    }

    /**
     * 伪随机带权重的数组
     *
     * @param a                随机数组
     * @param weightCalculator 权重计算器
     * @throws IllegalArgumentException 当c中元素通过权重计算器得到的权重为负数时
     */
    public static <T> T randomByWeight(WeightCalculator<T> weightCalculator, T[] a) {
        return randomByWeight(weightCalculator, Arrays.asList(a));
    }

    /**
     * 伪随机带权重的数组
     *
     * @param weightCalculator 权重计算器
     * @throws IllegalArgumentException 当c中元素通过权重计算器得到的权重为负数时
     */
    public static <T> List<T> randomListByWeight(WeightCalculator<T> weightCalculator, Collection<T> c, int count) {
        List<T> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            T t = randomByWeight(weightCalculator, c);
            result.add(t);
        }
        return result;
    }

    /**
     * 伪随机带权重的数组
     *
     * @param a                随机数组
     * @param weightCalculator 权重计算器
     * @throws IllegalArgumentException 当a中元素通过权重计算器得到的权重为负数时
     */
    public static <T> T[] randomArrayByWeight(WeightCalculator<T> weightCalculator, T[] a, int count) {
        @SuppressWarnings("unchecked")
        T[] ts = (T[]) new Object[count];
        for (int i = 0; i < count; i++) {
            ts[i] = randomByWeight(weightCalculator, a);
        }
        return ts;
    }

    /**
     * 伪随机数组
     *
     * @param a 随机数组
     * @throws IllegalArgumentException 当随机集合元素数量小于count
     */
    public static <T> T[] randomArrayNotRepeat(T[] a, T[] result) {
        for (int i = 0; i < result.length; i++) {
            int last = a.length - i - 1;
            int index = random(0, last);
            T m = a[last];
            result[i] = a[last] = a[index];
            a[index] = m;
        }
        return result;
    }

    /**
     * 伪随机数组
     *
     * @throws IllegalArgumentException 当c长度为0时
     */
    public static <T> List<T> randomList(Collection<T> c, int count) {
        List<T> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            T t = random(c);
            result.add(t);
        }
        return result;
    }

    /**
     * 伪随机数组
     *
     * @param a 随机数组
     * @throws IllegalArgumentException 当a长度为0时
     */
    public static <T> T[] randomArray(T[] a, int count) {
        @SuppressWarnings("unchecked")
        T[] ts = (T[]) new Object[count];
        for (int i = 0; i < count; i++) {
            ts[i] = random(a);
        }
        return ts;
    }

    /**
     * 伪随机集合中某个元素
     *
     * @param totalWeight      总权重
     * @param weightCalculator 权重计算器
     * @param c                随机库集合
     * @throws IllegalArgumentException 当totalWeight小于0时
     */
    public static <T> T randomByWeight(int totalWeight, WeightCalculator<T> weightCalculator, Collection<T> c) {
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("总权重不能小于0！");
        }
        int sum = 0;
        for (T t : c) {
            int weight = weightCalculator.weight(t);
            if (weight < 0) {
                throw new IllegalArgumentException("权重不能为负数：" + weight);
            }
            sum += weight;
        }
        if (totalWeight < sum) {
            throw new IllegalArgumentException("总权重不能小于集合的权重之和！");
        }

        int randVal = ThreadLocalRandom.current().nextInt(totalWeight);
        for (T t : c) {
            int weight = weightCalculator.weight(t);
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        return null;
    }

    /**
     * 伪随机集合中某个元素
     *
     * @param c 随机库集合
     * @throws IllegalArgumentException 当totalWeight小于0时
     */
    public static <T extends IWeight> T randomByWeight(Collection<T> c) {
        @SuppressWarnings(value = "unchecked")
        T[] ts = (T[]) new IWeight[c.size()];
        return randomByWeight(c.toArray(ts));
    }

    /**
     * 伪随机数组中某个元素
     *
     * @param c 随机库集合
     * @throws IllegalArgumentException 当某个权重为负数时
     * @throws IllegalArgumentException 当数组长度为0时
     */
    public static <T extends IWeight> T randomByWeight(T[] c) {
        if (c.length == 0) {
            throw new IllegalArgumentException("随机数组长度不能为0！");
        }
        int sum = 0;
        for (T t : c) {
            int weight = t.getWeight();
            if (weight < 0) {
                throw new IllegalArgumentException("权重不能为负数：" + weight);
            }
            sum += weight;
        }

        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : c) {
            int weight = t.getWeight();
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        throw new RuntimeException("随机出错！");
    }

    /**
     * 伪随机集合中一组元素
     *
     * @param c 随机库集合
     * @throws IllegalArgumentException 当集合长度为0时
     */
    public static <T extends IWeight> List<T> randomListByWeight(Collection<T> c, int count) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(randomByWeight(c));
        }
        return result;
    }

    /**
     * 伪随机数组中一组元素
     *
     * @param a 随机数组
     * @throws IllegalArgumentException 当数组长度为0时
     */
    public static <T extends IWeight> T[] randomArrayByWeight(T[] a, int count) {
        @SuppressWarnings("unchecked")
        T[] ts = (T[]) new IWeight[count];
        for (int i = 0; i < count; i++) {
            ts[i] = randomByWeight(a);
        }
        return ts;
    }

    /**
     * 伪随机数组中某个元素
     *
     * @param a           随机数组
     * @param totalWeight 总权重
     * @throws IllegalArgumentException 当数组长度为0时
     * @throws IllegalArgumentException 当totalWeight小于等于0时
     * @throws IllegalArgumentException 当元素某个权重为负数时
     * @throws IllegalArgumentException 当totalWeight小于所有元素权重之和
     */
    public static <T extends IWeight> T randomByWeight(T[] a, int totalWeight) {
        if (a.length == 0) {
            throw new IllegalArgumentException("随机数组长度不能为0！");
        }
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("总权重不能为非正数！");
        }
        int sum = 0;
        for (T t : a) {
            int weight = t.getWeight();
            if (weight < 0) {
                throw new IllegalArgumentException("权重不能小于0！");
            }
            sum += weight;
        }
        if (sum > totalWeight) {
            throw new IllegalArgumentException("总权重不能小于所有元素权重之和！");
        }

        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : a) {
            int weight = t.getWeight();
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        return null;
    }

    /**
     * 伪随机集合中某个元素
     *
     * @param c           随机集合
     * @param totalWeight 总权重
     * @throws IllegalArgumentException 当数组长度为0时
     * @throws IllegalArgumentException 当totalWeight小于等于0时
     * @throws IllegalArgumentException 当元素某个权重为负数时
     * @throws IllegalArgumentException 当totalWeight大于所有元素权重之和
     */
    public static <T extends IWeight> T randomByWeight(Collection<T> c, int totalWeight) {
        @SuppressWarnings("unchecked")
        T[] ts = (T[]) new IWeight[c.size()];
        return randomByWeight(ts, totalWeight);
    }

    /**
     * 伪随机数组中一组元素
     *
     * @param a           随机数组
     * @param totalWeight 总权重
     * @throws IllegalArgumentException 当数组长度为0时
     * @throws IllegalArgumentException 当totalWeight小于等于0时
     * @throws IllegalArgumentException 当元素某个权重为负数时
     * @throws IllegalArgumentException 当totalWeight小于所有元素权重之和
     */
    public static <T extends IWeight> T[] randomArrayByWeight(T[] a, int totalWeight, int count) {
        @SuppressWarnings("unchecked")
        T[] ts = (T[]) new IWeight[count];
        for (int i = 0; i < count; i++) {
            ts[i] = randomByWeight(a, totalWeight);
        }
        return ts;
    }

    /**
     * 伪随机集合中某个元素
     *
     * @param c 随机集合
     * @throws IllegalArgumentException 当数组长度为0时
     * @throws IllegalArgumentException 当totalWeight小于等于0时
     * @throws IllegalArgumentException 当元素某个权重为负数时
     * @throws IllegalArgumentException 当totalWeight大于所有元素权重之和
     */
    public static <T extends IWeight> List<T> randomListByWeight(Collection<T> c, int totalWeight, int count) {
        @SuppressWarnings("unchecked")
        T[] ts = (T[]) new IWeight[c.size()];
        T[] ts2 = randomArrayByWeight(c.toArray(ts), totalWeight, count);
        return new ArrayList<>(Arrays.asList(ts2));
    }

    public static int[] randomArrayNotRepeat(int[] a, int[] result) {
        for (int i = 0; i < result.length; i++) {
            int last = a.length - i - 1;
            int index = random(0, last);
            int m = a[last];
            result[i] = a[last] = a[index];
            a[index] = m;
        }
        return result;
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }
}
