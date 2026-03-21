package phishguard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:h2:~/phishguarddb";
    private static final String USER = "sa"; 
    private static final String PASSWORD = ""; 

    static {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("Connected to H2 database");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
