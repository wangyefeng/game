package org.wangyefeng.game.common.util;

import java.util.List;

public class ArrayUtil {

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(double[] arr, int i, int j) {
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(Object[] arr, int i, int j) {
        Object temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(List<Object> arr, int i, int j) {
        Object temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }
}
