package org.wangyefeng.game.common.random;

import junit.framework.TestCase;

import java.util.Arrays;

public class RandomUtilTest extends TestCase {

    private WeightArrayPool<Integer> pool = new WeightArrayPool<>(integer -> integer, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    /**
     * Create the test case
     */
    public RandomUtilTest() {
        super("RandomUtil");
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        Integer[] integers = new Integer[2];
        pool.randomUniqueArray(integers);
        System.out.println(Arrays.toString(integers));
    }
}
