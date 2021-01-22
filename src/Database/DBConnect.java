package Database;

import java.sql.*;

/**
 * class to handle connection to database
 */
public class DBConnect {
    public static final String driver = "com.mysql.cj.jdbc.Driver";
    public static final String database = "WJ06NQx";
    public static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/" + database;
    public static final String dbUser = "U06NQx";
    public static final String dbPass = "53688814265";
    public static Connection connection;

    /**
     * connects to provided server
     */
    public static void handleConnection()  {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, dbUser, dbPass);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * closes the connection to server when done
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    /**
     * checks user login credentials vs databse
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    public static boolean handleLogin(String username, String password) throws SQLException {
        boolean login = false;
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM users");
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        String[] comparison = new String[columnsNumber+1];
        while(rs.next()) {
            for(int i = 1; i <= columnsNumber; i++) {
                String columnVal = rs.getString(i);
                comparison[i] = columnVal;
            }
            if(comparison[2].equals(username) && comparison[3].equals(password)) {
                login = true;
                break;
            }
        }
        return login;
    }

    /**
     * returns connection for usage in other classes
     * @return
     */
    public static Connection getConnection() {
        return connection;
    }
}
