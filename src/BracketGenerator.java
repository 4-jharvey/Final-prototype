import java.util.*;
import java.sql.*;
import javax.swing.JOptionPane;
import java.time.*;

public class BracketGenerator {
    public static void generateBracket(int tournamentID){
        //connects to the database and gets the team data and puts them in a list
        try(Connection connect = DatabaseConnection.getConnection()){
            
            PreparedStatement delete = connect.prepareStatement("DELETE FROM Duel WHERE TournamentID = ?");
            delete.setInt(1, tournamentID);
            delete.executeUpdate();
            
            System.out.println("Cleared duels from tournament " + tournamentID);
            
            // grabs Team ID from the right tournament
            List<Integer> teamIDs = new ArrayList<>();
            String getTeams = "SELECT TeamID FROM Team WHERE TournamentID = ?";
            PreparedStatement psTeams = connect.prepareStatement(getTeams);
            psTeams.setInt(1, tournamentID);
            ResultSet rs = psTeams.executeQuery();

            while (rs.next()){
                teamIDs.add(rs.getInt("TeamID"));
            }
            
            //shuffles the teams so the matches are randomised
            Collections.shuffle(teamIDs);
            
            //creates the variables for the final
            Integer finalWinner = createWinnerBracket(connect, tournamentID, teamIDs, 1);

            
            
            //holds the data for the finale box
            if(finalWinner != null){
                String finale = "INSERT INTO Duel (TeamA, TeamB, Round, Winner, TournamentID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psFinal = connect.prepareStatement(finale, Statement.RETURN_GENERATED_KEYS);

                psFinal.setInt(1, finalWinner);
                psFinal.setNull(2, java.sql.Types.INTEGER);
                psFinal.setInt(3, 100);
                psFinal.setInt(4, finalWinner);
                psFinal.setInt(5, tournamentID);
                psFinal.executeUpdate();
                
                ResultSet rsKeys = psFinal.getGeneratedKeys();
                if(rsKeys.next()){
                    int matchID = rsKeys.getInt(1);
                    LocalDateTime matchTime = LocalDateTime.now().plusMinutes(30);
                    insertSchedule(connect, tournamentID, matchID, matchTime);
                }
            }    

            //debug statements
            System.out.println("Loaded " + teamIDs.size() + " teams.");
            System.out.println("Team IDs = " + teamIDs);
            System.out.println("TournamentID = " + tournamentID);
            System.out.println("Winner: " + finalWinner);

            

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

    //sets the losers list 

    private static Integer createWinnerBracket(Connection connect, int tournamentID, List<Integer> teamIDs, int round) throws SQLException{
    
        if(teamIDs.size() == 1){
            return teamIDs.get(0);
        }

        //stops any activity is happening if there are less than 2 teams in list
        if (teamIDs.size() < 2){
          return null;
        }
        
        Random Rand = new Random();
        
        
        
        //creates winner and loser list
        List<Integer> winners = new ArrayList<>();

        
        //recursive method to keep track of who wins and inserts them into winner
        for(int i = 0; i < teamIDs.size(); i += 2){
            if(i + 1 < teamIDs.size()){
                int teamA = teamIDs.get(i);
                int teamB = teamIDs.get(i + 1);
                
                int winner = Rand.nextBoolean() ? teamA : teamB;
                
                winners.add(winner);
                
                //inserts match data into database
                String insertWinnerDuel = "INSERT INTO Duel(TeamA, TeamB, Round, Winner, TournamentID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psWinnerDuel = connect.prepareStatement(insertWinnerDuel, Statement.RETURN_GENERATED_KEYS);
                psWinnerDuel.setInt(1, teamA);
                psWinnerDuel.setInt(2, teamB);
                psWinnerDuel.setInt(3, round);
                psWinnerDuel.setInt(4, winner);
                psWinnerDuel.setInt(5, tournamentID);
                
                psWinnerDuel.executeUpdate();
                
                ResultSet rsKeys = psWinnerDuel.getGeneratedKeys();
                if(rsKeys.next()){
                    int matchID = rsKeys.getInt(1);
                    LocalDateTime matchTime = LocalDateTime.now().plusMinutes(30 * (round + i / 2));
                    insertSchedule(connect, tournamentID, matchID, matchTime);
                }
            } 
            //this is in case there is an odd number of teams
            else if(round == 1){
                int oddTeam = teamIDs.get(i);
                
                //inserts match data into database
                String insertWinnerDuel = "INSERT INTO Duel(TeamA, TeamB, Round, Winner, TournamentID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psWinnerDuel = connect.prepareStatement(insertWinnerDuel, Statement.RETURN_GENERATED_KEYS);
                psWinnerDuel.setInt(1, oddTeam);
                psWinnerDuel.setNull(2, java.sql.Types.INTEGER);
                psWinnerDuel.setInt(3, round);
                psWinnerDuel.setInt(4, oddTeam);
                psWinnerDuel.setInt(5, tournamentID);
                psWinnerDuel.executeUpdate();
                
                winners.add(oddTeam);
                
                ResultSet rsKeys = psWinnerDuel.getGeneratedKeys();
                if(rsKeys.next()){
                    int matchID = rsKeys.getInt(1);
                    LocalDateTime matchTime = LocalDateTime.now().plusMinutes(30 * (round + i / 2));
                    insertSchedule(connect, tournamentID, matchID, matchTime);
                }
            }
          
        }
                
        //debugging statments
        System.out.println("Executed duel for round " + round);
        System.out.println("Winner advancing: " + winners);
        

        
        return createWinnerBracket(connect, tournamentID, winners, round + 1);
    }        
    
    private static void insertSchedule (Connection connect, int tournamentID, int matchID, LocalDateTime matchTime) throws SQLException {
        String scheduleQuery = "INSERT INTO Schedule (TournamentID, matchID, Time) VALUES (?, ?, ?)";
        PreparedStatement psSched = connect.prepareStatement(scheduleQuery);
        psSched.setInt(1, tournamentID);
        psSched.setInt(2, matchID);
        psSched.setTimestamp(3, Timestamp.valueOf(matchTime));
        psSched.executeUpdate();
    }
        
}

