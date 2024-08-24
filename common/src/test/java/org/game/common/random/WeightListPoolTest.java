package org.game.common.random;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class WeightListPoolTest extends TestCase {

    private WeightListPool<Integer> pool;

    private Map<Integer, Times> map;

    private int sumWeight;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        int size = 100;
        pool = new WeightListPool<>(size);
        map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            int random = RandomUtil.random(1, 10000);
            pool.add(i, random);
            sumWeight += random;
            map.put(i, new Times(random));
        }
        pool.remove(1);
        Times times = map.remove(1);
        sumWeight -= times.weight;
        pool.remove(10);
        Times times2 = map.remove(10);
        sumWeight -= times2.weight;
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        double d = 0.0001;
        int times = 100000000;
        for (int i = 0; i < times; i++) {
            map.get(pool.random()).num++;
        }
        map.forEach((key, times1) -> {
            double expected = times1.weight * 1.0 / sumWeight;
            double actual = map.get(key).num * 1.0 / times;
            assertTrue(Math.abs(actual - expected) < d);
        });
    }

    private static class Times {

        int num;

        int weight;

        public Times(int weight) {
            this.weight = weight;
        }
    }
}
