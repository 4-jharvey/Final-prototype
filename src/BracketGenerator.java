import java.util.*;
import java.sql.*;
import javax.swing.JOptionPane;


public class BracketGenerator {
    public static void generateBracket(int tournamentID){
        try(Connection connect = DatabaseConnection.getConnection()){
            List<Integer> teamIDs = new ArrayList<>();
            String getTeams = "Select TeamID FROM Team WHERE TournamentID = ?";
            PreparedStatement psTeams = connect.prepareStatement(getTeams);
            psTeams.setInt(1, tournamentID);
            ResultSet rs = psTeams.executeQuery();
            
            while (rs.next()){
                teamIDs.add(rs.getInt("teamID"));
            }
            
            System.out.println("DEBUG: Loaded " + teamIDs.size() + " teams.");
            System.out.println("DEBUG: Team IDs = " + teamIDs);
            
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
        
        System.out.println("DEBUG: Starting round " + round + " with " + teamIDs.size() + " teams");
    
        
            if (teamIDs.size() < 2){
              System.out.println("DEBUG: Stopping — not enough teams to create another round.");
              return;  
            }
                        
            List<Integer> Winners = new ArrayList<>();
            
            Random Rand = new Random();
            
            
            String insertDuel = "INSERT INTO Duel(TeamA, TeamB, Round, Winner, TournamentID) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDuel = connect.prepareStatement(insertDuel);
            
            for(int i = 0; i < teamIDs.size(); i += 2){
                if(i + 1 < teamIDs.size()){
                    int teamA = teamIDs.get(i);
                    int teamB = teamIDs.get(i + 1);
                    
                    int winner = Rand.nextBoolean() ? teamA : teamB;
                    Winners.add(winner);
                    
                    psDuel.setInt(1, teamA);
                    psDuel.setInt(2, teamB);
                    psDuel.setInt(3, round);
                    psDuel.setInt(4, winner);
                    psDuel.setInt(5, tournamentID);
                    
                    psDuel.addBatch();
                } 
                else{
                    Winners.add(teamIDs.get(i));
                }
            }
            
            psDuel.executeBatch();
            
            System.out.println("DEBUG: Executed duel batch for round " + round);
            System.out.println("DEBUG: Winners advancing: " + Winners);
            
            createRound(connect, tournamentID, Winners, round + 1);
    }
}

