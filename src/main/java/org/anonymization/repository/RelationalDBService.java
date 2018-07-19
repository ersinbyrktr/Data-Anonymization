package org.anonymization.repository;

import org.deidentifier.arx.AttributeType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class RelationalDBService {
    /*
    Creates start hierarachy for the given input column as required by ARX
    Example: for zip 123

    the star hierarchy will be:
                         ***
                          |
                         1**
                          |
                         12*
                          |
                         123
    */
    public AttributeType.Hierarchy.DefaultHierarchy createStarHierarchy(Connection con, String col, String table) throws
            SQLException {
        Statement st;
        try {
            st = con.createStatement();
            AttributeType.Hierarchy.DefaultHierarchy hierarchy = AttributeType.Hierarchy.create();
            ResultSet rs = st.executeQuery("select distinct " + col + " from " + table);
            while (rs.next()) {
                String val = rs.getString(col);
                if (val != null) {
                    String[] valHierarchy = new String[val.length() + 1];
                    valHierarchy[0] = val;
                    int index = 1;
                    for (int i = val.length() - 1; i >= 0; i--) {
                        StringBuilder sb = new StringBuilder(val);
                        sb.setCharAt(i, '*');
                        val = sb.toString();
                        sb = null;
                        valHierarchy[index] = val;
                        index++;
                    }

                    hierarchy.add(valHierarchy);
                } else {
                    String[] valHierarchy = new String[3];
                    valHierarchy[0] = "NULL";
                    valHierarchy[1] = "NULL";
                    valHierarchy[2] = "*";
                    hierarchy.add(valHierarchy);
                }
            }
            return hierarchy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates range hierarchy for the given conditions
     */
    public AttributeType.Hierarchy.DefaultHierarchy createRangeHierarchy(Connection con, String col, String table, RangeCondition... hierarchies) throws
            SQLException {
        Statement st;
        try {
            st = con.createStatement();
            AttributeType.Hierarchy.DefaultHierarchy hierarchy = AttributeType.Hierarchy.create();
            ResultSet rs = st.executeQuery("select distinct " + col + " from " + table);
            Arrays.sort(hierarchies);
            while (rs.next()) {
                int val = rs.getInt(col);
                hierarchy.add(getHierarchies(val, hierarchies));
            }
            return hierarchy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String[] getHierarchies(int val, RangeCondition[] hierarchies) {
        boolean foundMatch = false;
        String[] retVal = new String[hierarchies[0].getHierarchies().length + 1];
        for (RangeCondition rc : hierarchies) {
            switch (rc.getOperator()) {
                case "<":
                    if (val < rc.getVal()) {
                        retVal[0] = String.valueOf(val);
                        System.arraycopy(rc.getHierarchies(), 0, retVal, 1, rc.getHierarchies().length);
                        foundMatch = true;
                    }
                    break;
                case "=":
                    if (val < rc.getVal()) {
                        retVal[0] = String.valueOf(val);
                        System.arraycopy(rc.getHierarchies(), 0, retVal, 1, rc.getHierarchies().length);
                        foundMatch = true;
                    }
                    break;
                case ">":
                    if (val > rc.getVal()) {
                        retVal[0] = String.valueOf(val);
                        System.arraycopy(rc.getHierarchies(), 0, retVal, 1, rc.getHierarchies().length);
                        foundMatch = true;
                    }
                    break;
                case "<=":
                    if (val <= rc.getVal()) {
                        retVal[0] = String.valueOf(val);
                        System.arraycopy(rc.getHierarchies(), 0, retVal, 1, rc.getHierarchies().length);
                        foundMatch = true;
                    }
                    break;
                case ">=":
                    if (val >= rc.getVal()) {
                        retVal[0] = String.valueOf(val);
                        System.arraycopy(rc.getHierarchies(), 0, retVal, 1, rc.getHierarchies().length);
                        foundMatch = true;
                    }
                    break;
                default:
                    System.out.println("Unsupported Range operation");
            }
        }
        if (retVal == null) {
            Arrays.fill(retVal, "*");
        }
        return retVal;
    }

    public ResultSet executeQueryWithSuppression(Connection con, int l, String query, String table, String... cols) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from (select ");
        sb.append(String.join(",", cols));
        sb.append(" from ").append(table).append(" group by ").append(String.join(",", cols)).append(" having count(*)>=").append(l);
        sb.append(") b join ").append(table).append(" a on ");
        StringBuilder joinCond = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            joinCond.append("a." + cols[i] + "=b." + cols[i]);
            if (i + 1 != cols.length) {
                joinCond.append(" and ");
            }
        }

        sb.append(joinCond.toString());

        System.out.println(sb.toString());
        Statement st;
        try {
            st = con.createStatement();
            return st.executeQuery(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public ResultSet executeQuery(Connection con, String query) {
        Statement st;
        try {
            st = con.createStatement();
            return st.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
