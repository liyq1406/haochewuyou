package com.haoche51.buyerapp.util;

import com.haoche51.buyerapp.data.Brand;

import java.util.Comparator;


/**
 * @author xiaanming
 */
public class PinyinComparator implements Comparator<Brand> {

    public int compare(Brand o1, Brand o2) {
        if (o1.getSortLetter().equals("@")
                || o2.getSortLetter().equals("#")) {
            return -1;
        } else if (o1.getSortLetter().equals("#")
                || o2.getSortLetter().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetter().compareTo(o2.getSortLetter());
        }
    }

}
