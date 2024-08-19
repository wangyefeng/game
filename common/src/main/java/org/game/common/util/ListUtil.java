package org.game.common.util;

import java.util.List;

public class ListUtil {

    public static <T> void swap(List<T> arr, int i, int j) {
        T temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }
}
