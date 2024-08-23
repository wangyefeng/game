package org.game.common.random;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class RandomTest extends TestCase {

    private WeightArrayPool<IWeightImpl> pool;

    private IWeightImpl[] arr;

    /**
     * Create the test case
     */
    public RandomTest() {
        super("RandomUtil");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        arr = new IWeightImpl[100];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new IWeightImpl(i, RandomUtil.random(1, 1000));
        }
        pool = WeightArrayPool.createPool(arr);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        double d = 0.0001;
        int times = 100000000;
        Map<Integer, Times> map = new HashMap<>(5);
        int sumWeight = 0;
        for (int i = 0; i < arr.length; i++) {
            map.put(i, new Times());
            sumWeight += arr[i].weight;
        }
        for (int i = 0; i < times; i++) {
            map.get(pool.random().id).num++;
        }
        for (int i = 0; i < arr.length; i++) {
            IWeightImpl iWeight = arr[i];
            double expected = iWeight.weight * 1.0 / sumWeight;
            double actual = map.get(i).num * 1.0 / times;
            assertTrue(Math.abs(actual - expected) < d);
        }
    }

    private static class Times {

        int num;
    }

    private static class IWeightImpl implements IWeight {

        int id;

        int weight;

        public IWeightImpl(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }

        @Override
        public int weight() {
            return weight;
        }
    }
}
