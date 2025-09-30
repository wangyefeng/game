package org.wyf.game.common.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class WeightArrayPoolTest {

    private WeightArrayPool<IWeightImpl> pool;

    private IWeightImpl[] arr;

    @Test
    @Disabled
    public void testApp() {
        double d = 0.0001;
        int times = 100000000;
        Map<Integer, Times> map = new HashMap<>(arr.length);
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
            Assertions.assertTrue(Math.abs(actual - expected) < d);
        }
    }

    @BeforeEach
    protected void setUp() throws Exception {
        arr = new IWeightImpl[100];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new IWeightImpl(i, RandomUtil.random(1, 1000));
        }
        pool = WeightArrayPool.createPool(arr);
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
