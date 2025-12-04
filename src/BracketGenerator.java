import java.util.*;
import java.sql.*;
import javax.swing.JOptionPane;


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
            Integer finalLoser = lChampion;
            
            
            //holds the data for the finale box
            if(finalWinner != null  &&  finalLoser != null){
                String finale = "INSERT INTO Duel (TeamA, TeamB, Round, TournamentID, whichBracket) VALUES (?, ?, ?, ?, 'F')";
                PreparedStatement psFinal = connect.prepareStatement(finale);

                psFinal.setInt(1, finalWinner);
                psFinal.setInt(2, finalLoser);
                psFinal.setInt(3, 100);
                psFinal.setInt(4, tournamentID);
                psFinal.executeUpdate();
            }    

            //debug statements
            System.out.println("Loaded " + teamIDs.size() + " teams.");
            System.out.println("Team IDs = " + teamIDs);
            System.out.println("TournamentID = " + tournamentID);
            System.out.println("Winner: " + finalWinner);
            System.out.println("3rd place: " + finalLoser);



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
    private static Integer lChampion = null;
    private static List<Integer> allLosers = new ArrayList<>();

    private static Integer createWinnerBracket(Connection connect, int tournamentID, List<Integer> teamIDs, int round) throws SQLException{
    
        if(teamIDs.size() == 1){
            return teamIDs.get(0);
        }

        //stops any activity is happening if there are less than 2 teams in list
        if (teamIDs.size() < 2){
          return null;
        }
        
        Random Rand = new Random();
        
        //inserts match data into database
        String insertWinnerDuel = "INSERT INTO Duel(TeamA, TeamB, Round, Winner, TournamentID, whichBracket) VALUES (?, ?, ?, ?, ?, 'W')";
        PreparedStatement psWinnerDuel = connect.prepareStatement(insertWinnerDuel);
        
        //creates winner and loser list
        List<Integer> winners = new ArrayList<>();
        List<Integer> losers = new ArrayList<>();
        
        //recursive method to keep track of who wins and inserts them into winner
        for(int i = 0; i < teamIDs.size(); i += 2){
            if(i + 1 < teamIDs.size()){
                int teamA = teamIDs.get(i);
                int teamB = teamIDs.get(i + 1);
                
                int winner = Rand.nextBoolean() ? teamA : teamB;
                int loser = (winner == teamA) ? teamB : teamA;
                
                winners.add(winner);
                losers.add(loser);
                
                psWinnerDuel.setInt(1, teamA);
                psWinnerDuel.setInt(2, teamB);
                psWinnerDuel.setInt(3, round);
                psWinnerDuel.setInt(4, winner);
                psWinnerDuel.setInt(5, tournamentID);
                
                psWinnerDuel.addBatch();
            } 
            //this is in case there is an odd number of teams
            else if(round == 1){
                int oddTeam = teamIDs.get(i);
                
                psWinnerDuel.setInt(1, oddTeam);
                psWinnerDuel.setNull(2, java.sql.Types.INTEGER);
                psWinnerDuel.setInt(3, round);
                psWinnerDuel.setInt(4, oddTeam);
                psWinnerDuel.setInt(5, tournamentID);
                psWinnerDuel.addBatch();
                
                winners.add(oddTeam);
            }
          
        }
        allLosers.addAll(losers);
        
                psWinnerDuel.executeBatch();
        //debugging statments
        System.out.println("Executed duel for round " + round);
        System.out.println("Winner advancing: " + winners);
        
        Integer finalLoser = createLoserBracket(connect, tournamentID, losers, 2 * round - 1);
        if(finalLoser != null){
            lChampion = finalLoser;
        }
        
        return createWinnerBracket(connect, tournamentID, winners, round + 1);
    }        
        
    private static Integer createLoserBracket(Connection connect, int tournamentID, List<Integer> losers, int round) throws SQLException{
    
    if(losers.size() == 1){
        return losers.get(0);
    }

        //stops any activity is happening if there are less than 2 teams in list
        if (losers.size() < 2){
          return null;  
        }
        
        Random Rand = new Random();
        List<Integer> nextRound = new ArrayList<>();

        
        //inserts match data into database
        String loserDuel = "INSERT INTO Duel(TeamA, TeamB, Round, Winner, TournamentID, whichBracket) VALUES (?, ?, ?, ?, ?, 'L')";
        PreparedStatement psLoserDuel = connect.prepareStatement(loserDuel);           
        
        //recursive method to keep track of who wins and inserts them into winner
        for(int i = 0; i < losers.size(); i += 2){
            if(i + 1 < losers.size()){
                int teamA = losers.get(i);
                int teamB = losers.get(i + 1);
                
                int winner = Rand.nextBoolean() ? teamA : teamB;
                nextRound.add(winner);
                
                psLoserDuel.setInt(1, teamA);
                psLoserDuel.setInt(2, teamB);
                psLoserDuel.setInt(3, round);
                psLoserDuel.setInt(4, winner);
                psLoserDuel.setInt(5, tournamentID);
                
                psLoserDuel.addBatch();
            } 
            else{
                int oddTeam = losers.get(i);
                nextRound.add(oddTeam);
                
                psLoserDuel.setInt(1, oddTeam);
                psLoserDuel.setNull(2, java.sql.Types.INTEGER);
                psLoserDuel.setInt(3, round);
                psLoserDuel.setInt(4, oddTeam);
                psLoserDuel.setInt(5, tournamentID);
                psLoserDuel.addBatch();
                
            }
        } 
        
        
        psLoserDuel.executeBatch();
        System.out.println("Executed duel for round " + round);
        System.out.println("Winner advancing: " + nextRound);
        
        allLosers = new ArrayList<>(nextRound);
        return createLoserBracket(connect, tournamentID, nextRound, round + 1);

    }
}

