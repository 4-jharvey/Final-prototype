import java.util.*;
import java.sql.*;
import javax.swing.JOptionPane;
import java.time.*;

public class BracketGenerator {
    public static void generateBracket(int tournamentID){
        //connects to the database and gets the team data and puts them in a list
        try(Connection connect = DatabaseConnection.getConnection()){
            
            //Deletes any Duels from the database
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
            //calls on bracket creation
            if(teamIDs.size() >= 2){
                createWinnerBracket(connect, tournamentID, teamIDs, 1);
            }
            
            //calls on the find Winner statement to create the tournament winner box 
            Integer finalWinner = findWinner(connect, tournamentID, 1);

            
            
            //inserts the data about the finale box
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
                
                //creates some of the variables used to create the schedule
                if(rsKeys.next()){
                    int matchID = rsKeys.getInt(1);
                    
                    //changes the timings depending on the game
                    String sqlTime = "SELECT GameTitle FROM Tournament WHERE TournamentID = ?";
                    PreparedStatement psTime = connect.prepareStatement(sqlTime);
                    psTime.setInt(1, tournamentID);
                    ResultSet rsTime = psTime.executeQuery();
                    
                    String game = null;
                    if(rsTime.next()){
                        game = rs.getString("GameTitle");
                    }
                    
                    int time = 30;
                    
                    if (game != null){
                        switch(game){
                            case "Valorant":
                                time = 50;
                                break;
                            case "CSGO":
                                time = 50;
                                break;
                            case "Rocket League":
                                time = 10;
                                break;
                            case "Rainbow Six Siege":
                                time = 40;
                                break;
                        }
                            
                    }
                    //Timings for the matches
                    LocalDateTime matchTime = LocalDateTime.now().plusMinutes(time);
                    //5 minute break between matches
                    matchTime = matchTime.plusMinutes(5);
                    
                    //Timings for a Lunch Break
                    LocalTime lunchStart = LocalTime.of(12, 0);
                    LocalTime lunchEnd = LocalTime.of(13, 0);
                    
                    LocalTime matchEnd = matchTime.toLocalTime();
                    //If match is during lunch then it has to start after 13:00
                    if(matchEnd.isAfter(lunchStart) && matchEnd.isBefore(lunchEnd)){
                        matchTime = LocalDateTime.of(matchTime.toLocalDate(), lunchEnd);
                    }
                    insertSchedule(connect, tournamentID, matchID, matchTime);
                }
                
                //inserts tournament winner into the database
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
    
    //creates the bracket
    private static void createWinnerBracket(Connection connect, int tournamentID, List<Integer> teamIDs, int round) throws SQLException{
    

        //method to keep track of who wins and inserts them into winner
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
                //creates variables for the schedule generator to use
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
                
                //creates variables for the schedule generator to use
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
    
    //this method selects scores from duel table to find out who was the winner for each match
    private static Integer findWinner(Connection connect, int tournamentID, int startRound) throws SQLException{
        int round = startRound;
        while(true){
            List<Integer> Winners = new ArrayList<>();
            //selects scores from table + teams
            String sqlRound = "SELECT TeamA, TeamB, Winner, TeamA_Score, TeamB_Score "
                              + "FROM Duel "
                              + "WHERE TournamentID = ? AND Round = ?";
            PreparedStatement psRound = connect.prepareStatement(sqlRound);
            psRound.setInt(1, tournamentID);
            psRound.setInt(2, round);
            ResultSet rsDuel = psRound.executeQuery();
            
            boolean unfinishedMatch = false;
            
            while(rsDuel.next()){
                //used object as those variables can be null
                Integer teamA = rsDuel.getInt("TeamA");
                Integer teamB = rsDuel.getObject("TeamB", Integer.class);
                Integer winner = rsDuel.getObject("Winner", Integer.class);
                Integer scoreA = rsDuel.getObject("TeamA_Score", Integer.class);
                Integer scoreB = rsDuel.getObject("TeamB_Score", Integer.class);
                
                //in case of odd team
                if(teamB == null){
                    Winners.add(teamA);
                    continue;
                }
                //in case winner is already found
                if(winner != null){
                    Winners.add(winner);
                    continue;
                }
                //in case no scores are found
                if( scoreA == null || scoreB == null){
                    unfinishedMatch = true;
                    continue;
                }
                //determines who is the winner
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
            
            //if the match is not done
            if(unfinishedMatch){
                System.out.print("Unfinished match during round: " + round);
                return null;
            }
            //if there is only one winner left
            if(Winners.size() == 1){
                return Winners.get(0);
            }
            //Will only create a round if there are two or more teams
            if (Winners.size() >= 2){
                createWinnerBracket(connect, tournamentID, Winners, round + 1);
                round++;
                continue;
            }
  
            return null;
        }
    }
    
    //Updates the winner of the match
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
    
    //inserts any data required to create schedule into the schedule database
    private static void insertSchedule (Connection connect, int tournamentID, int matchID, LocalDateTime matchTime) throws SQLException {
        String scheduleQuery = "INSERT INTO Schedule (TournamentID, matchID, Time) VALUES (?, ?, ?)";
        PreparedStatement psSched = connect.prepareStatement(scheduleQuery);
        psSched.setInt(1, tournamentID);
        psSched.setInt(2, matchID);
        psSched.setTimestamp(3, Timestamp.valueOf(matchTime));
        psSched.executeUpdate();
    }
    
    // gets the team names by using the teamIDs
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
        //if no name is found
        return " ";
    }
        
}

