package com.example.send;

import java.util.Comparator;

/**
 * Created by root on 15-9-15.
 */
public final class CompareValues implements Comparator<Contacter> {
    @Override
    public int compare(Contacter left, Contacter right) {
        int ret = left.getSortKey().compareToIgnoreCase(right.getSortKey());
        return ret;
    }
}
