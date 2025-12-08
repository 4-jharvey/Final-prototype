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
    
    private static JSONObject apiConnector(String api, String game, String apiKey) throws Exception {
        URL apiUrl = new URL(api);
        HttpURLConnection connect = (HttpURLConnection) apiUrl.openConnection();
        connect.setRequestMethod("GET");
        /*
        if(game.equals("Rocket League")){
            connect.setRequestProperty("Authorisation", "Bearer " + apiKey);
        }
        if(game.equals("Rainbow Six Siege")){
            connect.setRequestProperty("Authorisation", apiKey);
        }
*/

        if(game.equals("Valorant")){
            connect.setRequestProperty("Authorisation", "HDEV-4878df4d-b931-48bc-9e7b-192ca532324e");
        }
            
        
        BufferedReader input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        StringBuilder reply = new StringBuilder();
        String line;
        while((line = input.readLine()) != null){
            reply.append(line);
        }
        input.close();
        
        return new JSONObject(reply.toString());
    }
    
    private static int[] readStats(JSONObject json, String game){
        int kills = 0;
        int deaths = 0;
        int assists = 0;
        int wins = 0;
        int losses = 0;
        
        switch(game){
            case "Valorant":
                JSONObject valorantStats = json.getJSONObject("data").getJSONObject("stats");
                kills = valorantStats.optInt("kills", 0);
                deaths = valorantStats.optInt("deaths", 0);
                assists = valorantStats.optInt("assists", 0);
                break;
            case "CSGO":
                JSONObject csgoStats = json.getJSONObject("statistics");
                kills = csgoStats.optInt("kills", 0);
                deaths = csgoStats.optInt("deaths", 0);
                assists = csgoStats.optInt("assists", 0);
                break;
            case "Rainbow Six Siege":
                JSONObject r6Stats = json.getJSONArray("Player").getJSONObject(0);
                kills = r6Stats.optInt("kills", 0);
                deaths = r6Stats.optInt("deaths", 0);
                wins = r6Stats.optInt("wins", 0);
                losses = r6Stats.optInt("losses", 0);
                break;
            case "Rocket League":
                JSONObject rlStats = json.getJSONObject("stats");
                kills = rlStats.optInt("goals", 0);
                assists = rlStats.optInt("assists", 0);
                wins = rlStats.optInt("wins", 0);
                losses = rlStats.optInt("losses", 0);
                break;
        
        }
        
        return new int[] {kills, deaths, assists, wins, losses};
    }
    
    public static void playerStats(Connection connect, int matchID, String Game, int teamID, String apiKey){
        try{
            String sql = "SELECT PlayerID, Username FROM Player WHERE TeamID = ?";
            PreparedStatement psPlayer = connect.prepareStatement(sql);
            psPlayer.setInt(1, teamID);
            ResultSet rsPlayer = psPlayer.executeQuery();
            
            while(rsPlayer.next()){
                int PlayerId = rsPlayer.getInt("PlayerID");
                String Username = rsPlayer.getString("Username");
                
                String api = API(Username, Game);
                JSONObject json = apiConnector(api, apiKey);
                
                int[] stats = readStats(json, Game);
                int kills = stats[0];
                int deaths = stats[1];
                int assists = stats[2];
                int wins = stats[3];
                int losses = stats[4];
                
                String statsSQL = "INSERT INTO Player_stats (PlayerID, Kills, Deaths, Assists, Wins, Losses) VALUES (?, ?, ?, ?, ?, ?)";
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
