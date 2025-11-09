import java.util.*;
import java.sql.*;
import javax.swing.JOptionPane;


public class BracketGenerator {
    public static void generateBracket(int tournamentID){
        try(Connection connect = DatabaseConnection.getConnection()){
            List<Integer> teamIDs = new ArrayList<>();
            String getTeams = "Select teamID FROM Team WHERE TournamentID = ?";
            PreparedStatement psTeams = connect.prepareStatement(getTeams);
            psTeams.setInt(1, tournamentID);
            ResultSet rs = psTeams.executeQuery();
            
            while (rs.next()){
                teamIDs.add(rs.getInt("teamID"));
            }
            
            Collections.shuffle(teamIDs);
            
            createRound(connect, tournamentID, teamIDs, 1);
                
        } catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
        }
        
    private static void createRound(Connection connect, int tournamentID, List<Integer> teamIDs, int round) throws SQLException{
            if (teamIDs.size() == 1){
              return;  
            }
            
            List<Integer> Winners = new ArrayList<>();
            
            Random Rand = new Random();
            
              int randNum = (int)(Math.random()* 1000000001);
              String MatchID = Integer.toString(randNum);
            
            String insertDuel = "INSERT INTO Duel(MatchID, TournamentID, TeamA, TeamB, Round, Winner) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psDuel = connect.prepareStatement(insertDuel);
            
            for(int i = 0; i <= teamIDs.size(); i += 2){
                if(i + 1 < teamIDs.size()){
                    int teamA = teamIDs.get(i);
                    int teamB = teamIDs.get(i + 1);
                    
                    int winner = Rand.nextBoolean() ? teamA : teamB;
                    Winners.add(winner);
                    
                    psDuel.setString(1, MatchID);
                    psDuel.setInt(2, tournamentID);
                    psDuel.setInt(3, teamA);
                    psDuel.setInt(4, teamB);
                    psDuel.setInt(5, round);
                    psDuel.setInt(6, winner);
                    
                    psDuel.addBatch();
                } 
                else{
                    Winners.add(teamIDs.get(i));
                }
            }
            
            psDuel.executeBatch();
            
            createRound(connect, tournamentID, teamIDs, round + 1);
    }
}

