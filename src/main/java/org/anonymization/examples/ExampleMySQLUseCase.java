package org.anonymization.examples;

import org.anonymization.repository.DatabaseConfig;
import org.anonymization.repository.MySqlService;
import org.anonymization.repository.RangeCondition;
import org.deidentifier.arx.ARXResult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.anonymization.examples.Example.processResults;
import static org.anonymization.examples.UseCaseExample.*;

public class ExampleMySQLUseCase {

    public static void main(String[] args) throws IOException, SQLException {
        DatabaseConfig mysqlConfig = new DatabaseConfig();
        String query = "SELECT users.first_name, users.age, users.country, users.zip, purchases.price, stores.address, products.brand " +
                "FROM ((purchases INNER JOIN users ON purchases.user_id = users.ID) INNER JOIN products ON purchases.product_id = products.id) INNER JOIN stores ON purchases.store_id = stores.id";
        mysqlConfig.setConnectStr("jdbc:mysql://18.185.114.122:3306");
        mysqlConfig.setDb("peng");
        mysqlConfig.setUser("peng");
        mysqlConfig.setPassword("admin");

        MySqlService ps = new MySqlService(mysqlConfig);
        Connection con = ps.getConnection();
        ResultSet rs = ps.executeQuery(con, query);
//        ResultSet rs = ps.executeQueryWithSuppression(con,4,query,"anon","disease");

        addDataToARX(rs);
        data.getDefinition().setAttributeType("zip", ps.createStarHierarchy(con, "zip", "users"));

        RangeCondition rc = new RangeCondition(40, "<", "<40", "*");
        RangeCondition rc1 = new RangeCondition(40, ">=", ">=40", "*");
        data.getDefinition().setAttributeType("age", ps.createRangeHierarchy(con, "age", "users", rc, rc1));
        ARXResult result = getResult();
        processResults(result);

    }

}
