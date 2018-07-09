package org.anonymization.repository;

import org.deidentifier.arx.AttributeType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RealationDBService {
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
    public AttributeType.Hierarchy.DefaultHierarchy createHierarchy(Connection con, String col, String table) throws
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

}
