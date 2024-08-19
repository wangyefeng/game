package org.wangyefeng;

import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("apple", "banana", "orange");
        list.forEach(s -> {
            if (s.equals("banana")) {
                return;
            }
            System.out.println(s);
        });
    }
}
