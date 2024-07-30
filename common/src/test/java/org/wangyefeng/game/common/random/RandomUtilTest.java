package org.wangyefeng.game.common.random;

import junit.framework.TestCase;

import java.util.Arrays;

public class RandomUtilTest extends TestCase {

    private WeightListPool<Integer> pool = new WeightListPool<>();

    /**
     * Create the test case
     */
    public RandomUtilTest() {
        super("RandomUtil");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pool.addPool(1, 1);
        pool.addPool(2, 2);
        pool.addPool(3, 3);
        pool.addPool(4, 4);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        Integer[] integers = pool.randomUniqueArray(new Integer[3]);
        System.out.println(Arrays.toString(integers));
    }
}
