package org.game.common.util;

import java.util.List;

public class ArrayUtil {

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(long[] arr, int i, int j) {
        long temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(double[] arr, int i, int j) {
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(float[] arr, int i, int j) {
        float temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(Object[] arr, int i, int j) {
        Object temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static <T> void swap(List<T> arr, int i, int j) {
        T temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }
}
