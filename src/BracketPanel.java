import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class BracketPanel extends JPanel{
    private int tournamentID;
    private Map<Integer, List<Duel>> rounds = new HashMap <>();
    
    public BracketPanel(int tournamentID){
        this.tournamentID = tournamentID;
        loadDuels();
    }
    
    private static class Duel{
        String teamA;
        String teamB;
        String winner;
        int round;
        
        Duel(String teamA, String teamB, String winner, int round){
            this.teamA = teamA;
            this.teamB = teamB;
            this.winner = winner;
            this.round = round;
        }
    }
    
    private void loadDuels(){
        try(Connection connect = DatabaseConnection.getConnection()){
            String query = "Select TeamA, TeamB, Winner, Round FROM Duel WHERE TournamentID = ? ORDER BY ASC";
            PreparedStatement ps = connect.prepareStatement(query);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                Duel Match = new Duel(
                        rs.getString("TeamA"),
                        rs.getString("TeamB"),
                        rs.getString("Winner"),
                        rs.getInt("Round")
                );
                
                if(!rounds.containsKey(Match.round)){
                    rounds.put(Match.round, new ArrayList<>());
                }
                rounds.get(Match.round).add(Match);
                
            }
                
            
        } catch (SQLException ex) {
            System.getLogger(BracketPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    private void drawBracket(Graphics2D graphics, int numOfRound, int x, int yStart, int spacing){
        List<Duel> Duels = rounds.get(numOfRound);
        if(Duels == null || Duels.isEmpty())return;
        
        int y = yStart;
        int boxWidth = 120;
        int boxHeight = 25;
        int lineOffsetX = 20;
        
        for(Duel Match : Duels){
            
            Color boxColour = new Color(0, 0, 0);
            Color borderColour = new Color(0, 0, 0);
            Color textColor = new Color(0, 0, 0);
           
        }
    }

}
