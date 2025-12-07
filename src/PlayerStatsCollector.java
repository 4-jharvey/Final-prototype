import java.sql.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import org.json.*;


public class PlayerStatsCollector {
    
    private static String API(String Username, String Game){
        switch(Game){
            case "Valorant":
                return "https://api.henrikdev.xyz/valorant/v1/account/" + Username;
            case "CSGO":
                return "https://hltv-api.vercel.app/api/player/" + Username;
                
            case "Rainbow Six Siege":
                return "https://r6tab.com/api/player/" + Username;
            case "Rocket League":
                return "https://ballchasing.com/api/player/" + Username;
            default:
                return null;
        }
    }
    
    private static JSONObject apiConnector(String api) throws Exception {
        URL apiUrl = new URL(api);
        HttpURLConnection connect = (HttpURLConnection) apiUrl.openConnection();
        connect.setRequestMethod("GET");
        
        BufferedReader input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        StringBuilder reply = new StringBuilder();
        String line;
        while((line = input.readLine()) != null){
            reply.append(line);
        }
        input.close();
        
        return new JSONObject(reply.toString());
    }
    
    public static void playerStats(Connection connect, int matchID, String Game, int teamID){
        try{
            String sql = "SELECT PlayerID, Username FROM Player WHERE TeamID = ?";
            PreparedStatement psPlayer = connect.prepareStatement(sql);
            psPlayer.setInt(1, teamID);
            ResultSet rsPlayer = psPlayer.executeQuery();
            
            while(rsPlayer.next()){
                int PlayerId = rsPlayer.getInt("PlayerID");
                String Username = rsPlayer.getString("Username");
                
                String api = API(Username, Game);
                JSONObject json = apiConnector(api);
                
                int kills = json.optInt("kills", 0);
                int deaths = json.optInt("deaths", 0);
                int assists = json.optInt("assists", 0);
                int wins = json.optInt("wins", 0);
                int losses = json.optInt("losses", 0);
                
                String statsSQL = "INSERT INTO Player_Stats (PlayerID, Kills, Deaths, Assists, Wins, Losses) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psStats = connect.prepareStatement(statsSQL);
                psStats.setInt(1, PlayerId);
                psStats.setInt(2, kills);
                psStats.setInt(3, deaths);
                psStats.setInt(4, assists);
                psStats.setInt(5, wins);
                psStats.setInt(6, losses);
                psStats.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
        
        
        
    }
}
