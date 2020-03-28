package com.cz.widget.recyclerview.sample;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author Created by cz
 * @date 2020-03-18 21:35
 * @email bingo110@126.com
 */
public class BinarySearchTest {

    @Test
    public void find(){
        int[] array={1,3,4,5,6,7};
        System.out.println(Arrays.binarySearch(array, 9));
    }
}
