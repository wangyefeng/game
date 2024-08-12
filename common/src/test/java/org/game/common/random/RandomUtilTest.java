package org.game.common.random;

import junit.framework.TestCase;

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
        EWeight weight = new EWeight(1, 2);
        EWeight weight2 = new EWeight(1, 1);
        System.out.println(weight.equals(weight2));
    }
}
