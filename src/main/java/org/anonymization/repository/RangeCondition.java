package org.anonymization.repository;

import lombok.Value;

import java.util.List;

/**
 * Currently supports only single condition like <,<=,= etc
 */
@Value
public class RangeCondition implements Comparable {
    int val;
    String operator;
    List<String> hierarchies;

    public String[] getHierarchies() {
        return hierarchies.toArray(String[]::new);
    }

    @Override
    public int compareTo(Object o) {
        RangeCondition rc = (RangeCondition) o;
        if (this.getVal() == rc.getVal()) {
            return 0;
        } else if (this.getVal() < rc.getVal()) {
            return -1;
        }
        return 1;
    }

}
