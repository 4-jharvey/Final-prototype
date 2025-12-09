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
            Integer finalWinner = findWinner(connect, tournamentID, 1);

            
            
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
                
                String Tourny = "UPDATE Tournament SET Tournament_winner = ? "
                                + "WHERE TournamentID = ? ";
                PreparedStatement psUpdate = connect.prepareStatement(Tourny);
                String winnerName = teamNames(connect, finalWinner);
                psUpdate.setString(1, winnerName);
                psUpdate.setInt(2, tournamentID);
                psUpdate.executeUpdate();
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

    private static void createWinnerBracket(Connection connect, int tournamentID, List<Integer> teamIDs, int round) throws SQLException{
    

        //recursive method to keep track of who wins and inserts them into winner
        for(int i = 0; i < teamIDs.size(); i += 2){
            if(i + 1 < teamIDs.size()){
                int teamA = teamIDs.get(i);
                int teamB = teamIDs.get(i + 1);
                
                //inserts match data into database
                String insertWinnerDuel = "INSERT INTO Duel(TeamA, TeamB, Round, Winner, TournamentID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psWinnerDuel = connect.prepareStatement(insertWinnerDuel, Statement.RETURN_GENERATED_KEYS);
                psWinnerDuel.setInt(1, teamA);
                psWinnerDuel.setInt(2, teamB);
                psWinnerDuel.setInt(3, round);
                psWinnerDuel.setNull(4, java.sql.Types.INTEGER);
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
        
    }        
    
    private static Integer findWinner(Connection connect, int tournamentID, int startRound) throws SQLException{
        int round = startRound;
        while(true){
            List<Integer> Winners = new ArrayList<>();
            
            String sqlRound = "SELECT TeamA, TeamB, Winner, TeamA_Score, TeamB_Score "
                              + "FROM Duel "
                              + "WHERE TournamentID = ? AND Round = ?";
            PreparedStatement psRound = connect.prepareCall(sqlRound);
            psRound.setInt(1, tournamentID);
            psRound.setInt(2, round);
            ResultSet rsDuel = psRound.executeQuery();
            
            boolean unfinishedMatch = false;
            
            while(rsDuel.next()){
                Integer teamA = rsDuel.getInt("TeamA");
                Integer teamB = rsDuel.getObject("TeamB", Integer.class);
                Integer winner = rsDuel.getObject("Winner", Integer.class);
                Integer scoreA = rsDuel.getObject("TeamA_Score", Integer.class);
                Integer scoreB = rsDuel.getObject("TeamB_Score", Integer.class);
                
                if(teamB == null){
                    Winners.add(teamA);
                    continue;
                }
                if(winner != null){
                    Winners.add(winner);
                    continue;
                }
                if( scoreA == null || scoreB == null){
                    unfinishedMatch = true;
                    continue;
                }
                if(scoreA > scoreB){
                    Winners.add(teamA);
                    setWinner(connect, tournamentID, round, teamA, teamB, teamA);
                } else if(scoreB > scoreA){
                    Winners.add(teamB);
                    setWinner(connect, tournamentID, round, teamA, teamB, teamB);
                } else {
                    unfinishedMatch = true;
                }
            }
            
            if(unfinishedMatch){
                System.out.print("Unfinished match during round: " + round);
                return null;
            }
            if(Winners.size() == 1){
                return Winners.get(0);
            }
            if (Winners.size() >= 2){
                createWinnerBracket(connect, tournamentID, Winners, round + 1);
                round++;
                continue;
            }
            
            return null;
        }
    }
    
    private static void setWinner(Connection connect, int tournamentID, int round, int teamA, Integer teamB, int winner) throws SQLException{
        String updateDuel = "UPDATE Duel SET Winner = ? "
                            + "WHERE TournamentID = ? AND Round = ? AND TeamA = ? AND TeamB = ?";
        PreparedStatement psUpdate = connect.prepareStatement(updateDuel);
        psUpdate.setInt(1, winner);
        psUpdate.setInt(2, tournamentID);
        psUpdate.setInt(3, round);
        psUpdate.setInt(4, teamA);
        psUpdate.setInt(5, teamB);
        psUpdate.executeUpdate();
    }
    
    private static void insertSchedule (Connection connect, int tournamentID, int matchID, LocalDateTime matchTime) throws SQLException {
        String scheduleQuery = "INSERT INTO Schedule (TournamentID, matchID, Time) VALUES (?, ?, ?)";
        PreparedStatement psSched = connect.prepareStatement(scheduleQuery);
        psSched.setInt(1, tournamentID);
        psSched.setInt(2, matchID);
        psSched.setTimestamp(3, Timestamp.valueOf(matchTime));
        psSched.executeUpdate();
    }
    
    private static String teamNames(Connection connect, int teamID) throws SQLException{
        try{
            String names = "SELECT TeamName FROM Team "
                           + "WHERE TeamID = ?";
            PreparedStatement psNames = connect.prepareStatement(names);
            psNames.setInt(1, teamID);
            ResultSet Names = psNames.executeQuery();
            
            if(Names.next()){
                return Names.getString("TeamName");
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }
        return " ";
    }
        
}

