package puzzle8;

import java.sql.ResultSet;

public class Query extends KoneksiDatabase {
    public Query() {
        super();
    }

    public ResultSet getData(){
        String sql = "SELECT * FROM data";
        try {
            rs = stmt.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public void insertData(int highScore, String bestTime) {
        try {
            String sql = "INSERT INTO data (highscore, waktu) VALUES ('%d', '%s')";
            sql = String.format(sql, highScore, bestTime);
            execute(sql);
            // stmt.close();
            // conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(String query) {
        try{
            stmt.execute(query);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
