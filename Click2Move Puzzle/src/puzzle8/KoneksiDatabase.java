package puzzle8;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

abstract class KoneksiDatabase {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/puzzle8s";
    static final String USER = "kelompok1a";
    static final String PASS = "123456789";

    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    
    public KoneksiDatabase(){
        
        try {
            // register driver
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            
//            System.out.println("koneksi berhasil");

        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("koneksi gagal");
        }
    }
    public abstract void execute(String query);
}