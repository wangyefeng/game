package org.wangyefeng.game.common.random;


import org.wangyefeng.game.common.util.ArrayUtil;
import org.wangyefeng.game.common.util.Assert;

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
     * @param min 边界值1
     * @param max 边界值2
     * @return [min, max]的某个数
     * @throws IllegalArgumentException 当min == max时
     */
    public static int random(int min, int max) {
        Assert.isTrue(max >= min, "非法随机参数：min：" + min + " max:" + max);
        if (max == min) {
            return min;
        }
        int bound = Math.abs(min - max) + 1;
        int num = ThreadLocalRandom.current().nextInt(bound);
        return min + num;
    }

    /**
     * 伪随机两个值之间的数
     *
     * @param min 边界值1
     * @param max 边界值2
     * @return [min, max]的某个数
     * @throws IllegalArgumentException 当min == max时
     */
    public static long random(long min, long max) {
        Assert.isTrue(max >= min, "非法随机参数：min：" + min + " max:" + max);
        if (max == min) {
            return min;
        }
        long bound = Math.abs(min - max) + 1;
        long num = ThreadLocalRandom.current().nextLong(bound);
        return min + num;
    }

    /**
     * 伪随机两个值之间的数
     *
     * @param min 边界值1
     * @param max 边界值2
     * @return [min, max)的某个数
     */
    public static double random(double min, double max) {
        Assert.isTrue(max >= min, "非法随机参数：min：" + min + " max:" + max);
        if (max == min) {
            return min;
        }
        double r = RandomUtil.nextDouble();
        return r * (max - min) + min;
    }

    /**
     * 伪随机两个值之间的数
     *
     * @param min 边界值1
     * @param max 边界值2
     * @return [min, max]的某个数
     */
    public static float random(float min, float max) {
        Assert.isTrue(max >= min, "非法随机参数：min：" + min + " max:" + max);
        if (max == min) {
            return min;
        }
        float r = ThreadLocalRandom.current().nextFloat();
        return r * (max - min) + min;
    }

    /**
     * 伪随机出集合中某个元素
     *
     * @param pool 随机池
     * @return 返回库中的某个元素
     * @throws NullPointerException     当c为null时
     * @throws IllegalArgumentException 当c没有元素时
     */
    public static <T> T random(List<T> pool) {
        Assert.isTrue(pool != null && !pool.isEmpty(), "随机库元素数量不能为0");
        int size = pool.size();
        int index = random(0, size - 1);
        return pool.get(index);
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
        Assert.isTrue(length > 0, "随机数组长度必须大于0");
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
        Assert.isTrue(length > 0, "随机数组长度必须大于0");
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
    public static long random(long[] a) {
        int length = a.length;
        Assert.isTrue(length > 0, "随机数组长度必须大于0");
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
    public static double random(double[] a) {
        int length = a.length;
        Assert.isTrue(length > 0, "随机数组长度必须大于0");
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
    public static float random(float[] a) {
        int length = a.length;
        Assert.isTrue(length > 0, "随机数组长度必须大于0");
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
        Assert.isTrue(length > 0, "随机数组长度必须大于0");
        int index = random(0, length - 1);
        return a[index];
    }

    /**
     * 伪随机带权重的集合
     *
     * @param pool             随机库集合
     * @param weightCalculator 权重计算器
     * @throws IllegalArgumentException 当c中元素通过权重计算器得到的权重为负数时
     */
    public static <T> T randomByWeight(Collection<T> pool, WeightCalculator<T> weightCalculator) {
        Assert.isTrue(pool != null && !pool.isEmpty(), "随机库元素数量不能为空");
        if (pool.size() == 1) {
            return pool.iterator().next();
        }
        int sum = 0;
        for (T t : pool) {
            int weight = weightCalculator.weight(t);
            Assert.isTrue(weight >= 0, "权重不能为负数 weight:" + weight);
            sum += weight;
        }
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : pool) {
            int weight = weightCalculator.weight(t);
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        // 永远不会执行到这里
        throw new RuntimeException();
    }

    /**
     * 伪随机带权重的数组
     *
     * @param pool             随机数组
     * @param weightCalculator 权重计算器
     * @throws IllegalArgumentException 当c中元素通过权重计算器得到的权重为负数时
     */
    public static <T> T randomByWeight(T[] pool, WeightCalculator<T> weightCalculator) {
        Assert.isTrue(pool != null && pool.length == 0, "随机库元素数量不能为空");
        if (pool.length == 1) {
            return pool[0];
        }
        int sum = 0;
        for (T t : pool) {
            int weight = weightCalculator.weight(t);
            Assert.isTrue(weight >= 0, "权重不能为负数 weight:" + weight);
            sum += weight;
        }
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : pool) {
            int weight = weightCalculator.weight(t);
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        // 永远不会执行到这里
        throw new RuntimeException();
    }


    /**
     * 伪随机集合中某个元素
     *
     * @param totalWeight      总权重
     * @param weightCalculator 权重计算器
     * @param pool             随机库集合
     * @throws IllegalArgumentException 当totalWeight小于0时
     */
    public static <T> T randomByWeight(Collection<T> pool, WeightCalculator<T> weightCalculator, int totalWeight) {
        Assert.isTrue(totalWeight > 0, "总权重不能小于0！!! totalWeight：" + totalWeight);
        int sum = 0;
        for (T t : pool) {
            int weight = weightCalculator.weight(t);
            Assert.isTrue(weight >= 0, "权重不能为负数 weight:" + weight);
            sum += weight;
        }
        Assert.isTrue(totalWeight >= sum, "总权重不能小于集合的权重之和！");
        int randVal = ThreadLocalRandom.current().nextInt(totalWeight);
        for (T t : pool) {
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
     * @param pool 随机池
     * @throws IllegalArgumentException 当totalWeight小于0时
     */
    public static <T extends IWeight> T randomByWeight(Collection<T> pool) {
        Assert.isTrue(pool != null && pool.size() > 0, "随机池不能为空！");
        if (pool.size() == 1) {
            return pool.iterator().next();
        }
        int sum = 0;
        for (T t : pool) {
            int weight = t.weight();
            Assert.isTrue(weight >= 0, "权重不能为负数：" + weight);
            sum += weight;
        }
        Assert.isTrue(sum > 0, "随机权重总和不能为0！");
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : pool) {
            int weight = t.weight();
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        throw new RuntimeException("随机出错！");
    }

    /**
     * 伪随机数组中某个元素
     *
     * @param pool 随机库集合
     * @throws IllegalArgumentException 当某个权重为负数时
     * @throws IllegalArgumentException 当数组长度为0时
     */
    public static <T extends IWeight> T randomByWeight(T[] pool) {
        Assert.isTrue(pool != null && pool.length > 0, "随机数组长度不能为0！");
        if (pool.length == 1) {
            return pool[0];
        }
        int sum = 0;
        for (T t : pool) {
            int weight = t.weight();
            Assert.isTrue(weight >= 0, "权重不能为负数：" + weight);
            sum += weight;
        }
        Assert.isTrue(sum > 0, "随机权重总和不能为0！");
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : pool) {
            int weight = t.weight();
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        throw new RuntimeException("随机出错！");
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
        Assert.isTrue(a != null && a.length > 0, "随机池不能为空！");
        Assert.isTrue(totalWeight > 0, "总权重不能为非正数！");
        int sum = 0;
        for (T t : a) {
            int weight = t.weight();
            Assert.isTrue(weight >= 0, "权重不能为负数：" + weight);
            sum += weight;
        }
        Assert.isTrue(sum > 0, "总权重不能大于所有元素权重之和！");
        Assert.isTrue(sum <= totalWeight, "总权重不能大于所有元素权重之和！");
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : a) {
            int weight = t.weight();
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
     * @param pool           随机集合
     * @param totalWeight 总权重
     * @throws IllegalArgumentException 当数组长度为0时
     * @throws IllegalArgumentException 当totalWeight小于等于0时
     * @throws IllegalArgumentException 当元素某个权重为负数时
     * @throws IllegalArgumentException 当totalWeight大于所有元素权重之和
     */
    public static <T extends IWeight> T randomByWeight(Collection<T> pool, int totalWeight) {
        Assert.isTrue(pool != null && pool.size() > 0, "随机池长度不能为0！");
        Assert.isTrue(totalWeight > 0, "总权重不能为非正数！");
        int sum = 0;
        for (T t : pool) {
            int weight = t.weight();
            Assert.isTrue(weight >= 0, "权重不能为负数：" + weight);
            sum += weight;
        }
        Assert.isTrue(sum > 0, "总权重不能大于所有元素权重之和！");
        Assert.isTrue(sum <= totalWeight, "总权重不能大于所有元素权重之和！");
        int randVal = ThreadLocalRandom.current().nextInt(sum);
        for (T t : pool) {
            int weight = t.weight();
            if (randVal < weight) {
                return t;
            }
            randVal -= weight;
        }
        return null;
    }


    /**
     * 伪随机数组
     *
     * @param a 随机数组  注意：这里为了效率，不会进行复制，而是直接修改原数组，因此调用者需要注意数组位置会改变，但数组的长度不会改变，内容也不会改变
     * @throws IllegalArgumentException 当随机集合元素数量小于count
     */
    public static <T> T[] randomArrayNotRepeat(T[] a, T[] result) {
        Assert.isTrue(a.length >= result.length, "随机数组长度必须大于等于结果数组长度");
        if (a.length == result.length) {
            System.arraycopy(a, 0, result, 0, a.length);
        } else if (result.length > 0) {
            for (int i = 0; i < result.length; i++) {
                int last = a.length - i - 1;
                int index = random(0, last);
                ArrayUtil.swap(a, index, last);
                result[i] = a[last];
            }
        }
        return result;
    }

    /**
     * 伪随机数组
     *
     * @param a 随机数组  注意：这里为了效率，不会进行复制，而是直接修改原数组，因此调用者需要注意数组位置会改变，但数组的长度不会改变，内容也不会改变
     * @throws IllegalArgumentException 当随机集合元素数量小于count
     */
    public static int[] randomArrayNotRepeat(int[] a, int[] result) {
        Assert.isTrue(a.length >= result.length, "随机数组长度必须大于等于结果数组长度");
        if (a.length == result.length) {
            System.arraycopy(a, 0, result, 0, a.length);
        } else {
            for (int i = 0; i < result.length; i++) {
                int last = a.length - i - 1;
                int index = random(0, last);
                ArrayUtil.swap(a, index, last);
                result[i] = a[last];
            }
        }
        return result;
    }

    /**
     * 伪随机数组
     *
     * @param a 随机数组  注意：这里为了效率，不会进行复制，而是直接修改原数组，因此调用者需要注意数组位置会改变，但数组的长度不会改变，内容也不会改变
     * @throws IllegalArgumentException 当随机集合元素数量小于count
     */
    public static long[] randomArrayNotRepeat(long[] a, long[] result) {
        Assert.isTrue(a.length >= result.length, "随机数组长度必须大于等于结果数组长度");
        if (a.length == result.length) {
            System.arraycopy(a, 0, result, 0, a.length);
        } else {
            for (int i = 0; i < result.length; i++) {
                int last = a.length - i - 1;
                int index = random(0, last);
                ArrayUtil.swap(a, index, last);
                result[i] = a[last];
            }
        }
        return result;
    }

    /**
     * 伪随机数组
     *
     * @param a 随机数组  注意：这里为了效率，不会进行复制，而是直接修改原数组，因此调用者需要注意数组位置会改变，但数组的长度不会改变，内容也不会改变
     * @throws IllegalArgumentException 当随机集合元素数量小于count
     */
    public static double[] randomArrayNotRepeat(double[] a, double[] result) {
        Assert.isTrue(a.length >= result.length, "随机数组长度必须大于等于结果数组长度");
        if (a.length == result.length) {
            System.arraycopy(a, 0, result, 0, a.length);
        } else {
            for (int i = 0; i < result.length; i++) {
                int last = a.length - i - 1;
                int index = random(0, last);
                ArrayUtil.swap(a, index, last);
                result[i] = a[last];
            }
        }
        return result;
    }

    /**
     * 伪随机数组
     *
     * @param a 随机数组  注意：这里为了效率，不会进行复制，而是直接修改原数组，因此调用者需要注意数组位置会改变，但数组的长度不会改变，内容也不会改变
     * @throws IllegalArgumentException 当随机集合元素数量小于count
     */
    public static float[] randomArrayNotRepeat(float[] a, float[] result) {
        Assert.isTrue(a.length >= result.length, "随机数组长度必须大于等于结果数组长度");
        if (a.length == result.length) {
            System.arraycopy(a, 0, result, 0, a.length);
        } else {
            for (int i = 0; i < result.length; i++) {
                int last = a.length - i - 1;
                int index = random(0, last);
                ArrayUtil.swap(a, index, last);
                result[i] = a[last];
            }
        }
        return result;
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }
}
