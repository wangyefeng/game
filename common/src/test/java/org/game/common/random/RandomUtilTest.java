package org.game.common.random;

import junit.framework.TestCase;

import java.util.Arrays;

public class RandomUtilTest extends TestCase {

    private WeightArrayPool<IWeightImpl> pool;

    /**
     * Create the test case
     */
    public RandomUtilTest() {
        super("RandomUtil");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IWeightImpl[] arr = new IWeightImpl[5];
        arr[0] = new IWeightImpl(1, 10);
        arr[1] = new IWeightImpl(2, 20);
        arr[2] = new IWeightImpl(3, 30);
        arr[3] = new IWeightImpl(4, 20);
        arr[4] = new IWeightImpl(5, 1000);
        pool = new WeightArrayPool<>(arr);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        IWeightImpl[] ss = pool.randomArray(new IWeightImpl[4]);
        System.out.println(Arrays.toString(ss));
    }

    private static class IWeightImpl implements IWeight {

        private int id;

        private int weight;

        public IWeightImpl(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }

        @Override
        public int weight() {
            return weight;
        }

        @Override
        public String toString() {
            return id + "";
        }
    }
}
