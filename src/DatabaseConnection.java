import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection{
    
    public static Connection getConnection() throws SQLException{
        Connection connect = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(
                    "jdbc:mysql://185.156.138.148:3306/4-jharvey",
                    "4-jharvey",
                    "Forget6-Building-Full"
            );
            System.out.print(connect);
            
        } catch(ClassNotFoundException ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE,null,ex);
        } catch (SQLException ex) {
            System.getLogger(DatabaseConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return connect;
    }
}