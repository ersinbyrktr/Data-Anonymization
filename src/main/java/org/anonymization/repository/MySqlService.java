package org.anonymization.repository;

import org.deidentifier.arx.AttributeType;

import java.sql.*;

public class MySqlService {
    private static DatabaseConfig dbconfig =null;
        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("MySql sql driver is not included. Please include the required postgres jar and rerun the program");
                System.exit(1);
            }
        }

        public MySqlService(DatabaseConfig dbconfig) {
            this.dbconfig=dbconfig;
        }

        public Connection getConnection(){
            Connection con=null;
            try {
                con = DriverManager.getConnection(dbconfig.getConnectStr()+"/"+dbconfig.getDb(), dbconfig.getUser(), dbconfig.getPassword());
            }catch (SQLException sqle){
                sqle.printStackTrace();
            }
            return con;
        }

        public ResultSet executeQuery(Connection con, String query){
            Statement st;
            try{
            st=con.createStatement();
            return  st.executeQuery(query);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }


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
       public AttributeType.Hierarchy.DefaultHierarchy createHierarchy(Connection con, String col,String table){
           Statement st;
           try{
               st=con.createStatement();
               AttributeType.Hierarchy.DefaultHierarchy hierarchy= AttributeType.Hierarchy.create();;
               ResultSet rs =st.executeQuery("select distinct "+col+" from "+table);
               while (rs.next()){
                   String val=rs.getString(col);
                   if(val!=null) {
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
                   }else{
                       String[] valHierarchy = new String[3];
                       valHierarchy[0] = "empty";
                       valHierarchy[1] = "empty";
                       valHierarchy[2] = "*";
                       hierarchy.add(valHierarchy);
                   }
               }

               return hierarchy;
           }catch (Exception e){
               e.printStackTrace();
               return null;
           }
       }
}
