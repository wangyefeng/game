package org.game.common.random;

import junit.framework.TestCase;
import org.junit.Assert;

public class RandomTest extends TestCase {

    private WeightArrayPool<IWeightImpl> pool;

    /**
     * Create the test case
     */
    public RandomTest() {
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
        arr[4] = new IWeightImpl(5, 50);
        pool = new WeightArrayPool<>(arr);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        Assert.assertEquals(0, pool.binarySearch(0));
        Assert.assertEquals(1, pool.binarySearch(10));
        Assert.assertEquals(2, pool.binarySearch(30));
        Assert.assertEquals(2, pool.binarySearch(31));
        Assert.assertEquals(4, pool.binarySearch(129));
        Assert.assertEquals(-1, pool.binarySearch(130));
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
