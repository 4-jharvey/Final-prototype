import java.util.*;
import java.sql.*;
import javax.swing.JOptionPane;


public class BracketGenerator {
    public static void generateBracket(int tournamentID){
        //connects to the database and gets the team data and puts them in a list
        try(Connection connect = DatabaseConnection.getConnection()){
            
            // grabs Team ID from the right tournament
            List<Integer> teamIDs = new ArrayList<>();
            String getTeams = "SELECT TeamID FROM Team WHERE TournamentID = ?";
            PreparedStatement psTeams = connect.prepareStatement(getTeams);
            psTeams.setInt(1, tournamentID);
            ResultSet rs = psTeams.executeQuery();
            
            while (rs.next()){
                teamIDs.add(rs.getInt("TeamID"));
            }
            
            //debug statements
            System.out.println("DEBUG: Loaded " + teamIDs.size() + " teams.");
            System.out.println("DEBUG: Team IDs = " + teamIDs);
            System.out.println("DEBUG Generator called with tournamentID = " + tournamentID);
            
            Collections.shuffle(teamIDs);
            
            createRound(connect, tournamentID, teamIDs, 1);
                //catches any errors
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
        //debug statement to make sure teams are being connected
        System.out.println("DEBUG: Starting round " + round + " with " + teamIDs.size() + " teams");
    
            //stops any activity is happening if there are less than 2 teams in list
            if (teamIDs.size() < 2){
              System.out.println("DEBUG: Stopping — not enough teams to create another round.");
              return;  
            }
            //list of all winner of each match            
            List<Integer> Winners = new ArrayList<>();
            
            Random Rand = new Random();
            
            //inserts match data into database
            String insertDuel = "INSERT INTO Duel(TeamA, TeamB, Round, Winner, TournamentID) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDuel = connect.prepareStatement(insertDuel);
            
            //recursive method to keep track of who wins and inserts them into winner
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
            //debugging statments
            System.out.println("DEBUG: Executed duel batch for round " + round);
            System.out.println("DEBUG: Winners advancing: " + Winners);
            
            createRound(connect, tournamentID, Winners, round + 1);
    }
}

