package org.anonymization.repository;

/**
 * Currently supports only single condition like <,<=,= etc
 */
public class RangeCondition implements Comparable {
    private int val;
    private String operator;
    private String[] hierarchies;

    public RangeCondition(int val, String operator, String... hierarchies) {
        this.val = val;
        this.operator = operator;
        this.hierarchies = hierarchies;
    }

    public int getVal() {
        return val;
    }

    public String getOperator() {
        return operator;
    }

    public String[] getHierarchies() {
        return hierarchies;
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
