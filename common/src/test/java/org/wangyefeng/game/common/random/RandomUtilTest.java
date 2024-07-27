package org.wangyefeng.game.common.random;

import junit.framework.TestCase;

public class RandomUtilTest extends TestCase {

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
        int num = 10000;
        int min = 1;
        int max = 10;
        for (int i = 0; i < num; i++) {
            int random = RandomUtil.random(min, max);
            assertTrue(random >= min && random <= max);
        }
    }
}
