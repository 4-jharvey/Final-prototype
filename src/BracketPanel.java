import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class BracketPanel extends JPanel{
    private int tournamentID;
    //creates hashmap for all rounds/ duels that will happen
    private Map<Integer, java.util.List<Duel>> rounds = new HashMap <>();
   
    //creates a background for the panel
    public BracketPanel(int tournamentID){
        this.tournamentID = tournamentID;
        Color Background = new Color(255,255,255);
        setBackground(Background);
        loadDuels();
    }
    
    //holds the data for each duel
    private static class Duel{
        String teamAName;
        String teamBName;
        boolean aWins;
        boolean bWins;
        int round;
        
        Duel(String teamAName, String teamBName, boolean aWins, boolean bWins, int round){
            this.teamAName = teamAName;
            this.teamBName = teamBName;
            this.aWins = aWins;
            this.bWins = bWins;
            this.round = round;
        }
    }
    
    
    private void loadDuels(){
        //connects to the database and extracts names of all the teams in ascending order
        try(Connection connect = DatabaseConnection.getConnection()){
            String query = "SELECT Duel.MatchID, Duel.Round, Duel.TeamA AS TeamAID, Duel.TeamB AS TeamBID, TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, Duel.Winner "
                           + "FROM Duel "
                           + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID "
                           +" JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID "
                           + "WHERE Duel.TournamentID = ? "
                           + "ORDER BY Duel.Round ASC";
            PreparedStatement ps = connect.prepareStatement(query);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();
            
            //this will grab the data from the query and willl but them in a list of all the matches and its results
            while(rs.next()){
                String teamAName = rs.getString("TeamAName");
                String teamBName = rs.getString("TeamBName");
                int TeamAID = rs.getInt("TeamAID");
                int TeamBID = rs.getInt("TeamBID");               
                int round = rs.getInt("Round");
                int winnerID = rs.getInt("Winner");
                
                
                boolean aWins = (winnerID == TeamAID);
                boolean bWins = (winnerID == TeamBID);

                java.util.List<Duel> listOfMatches = rounds.get(round);
                if(listOfMatches == null){
                    listOfMatches = new ArrayList<>();
                    rounds.put(round, listOfMatches);
                }
                listOfMatches.add(new Duel(teamAName, teamBName, bWins, aWins, round));
                
            }
                
            // catches any errors
        } catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.getMessage());
        }
    }
    
    //allows for pogram to customize painting
    @Override
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        drawBracket((Graphics2D) graphics);
    }
    
    // creates the boxes and connector lines for the bracket
    private void drawBracket(Graphics2D graphics){
        
        //panel measurements
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        //box measurements and number of rounds
        int numOfRounds = rounds.size();
        int boxWidth = 150;
        int boxHeight = 50;
        int horizontalSpacing = 180;
        int verticalSpacing = 40;
        
        //the final duel box and spacing for each side of the bracket
        int centre = panelWidth / 2;
        
        //each side of the bracket
        drawSide(graphics, centre, numOfRounds, panelHeight, boxWidth, boxHeight, horizontalSpacing, verticalSpacing, true);
        drawSide(graphics, centre, numOfRounds, panelHeight, boxWidth, boxHeight, horizontalSpacing, verticalSpacing, false);
        
        //draws final box
        graphics.drawRect(centre - boxWidth / 2, panelHeight / 2 - boxHeight / 2, boxWidth, boxHeight);   
    }
    // the class that draws each side
    private void drawSide(Graphics2D graphics, int centre, int numOfRounds, int panelHeight, int boxWidth, int boxHeight, int horizontalSpacing, int verticalSpacing, boolean isLeft){
        
        //customizable colour scheme
        Color boxBorder = new Color(0, 0, 0);
        Color textColour = new Color(0, 0, 0);
        Color connector = new Color(0, 0, 0);
        
        int side = isLeft ? -1:1;
        
        for(int round = 1; round <= numOfRounds; round++){
            
            java.util.List<Duel> duels = rounds.get(round);
            if (duels == null) continue;
            
            int boxX = centre + side * round * horizontalSpacing;
            
            int tournyHeight = duels.size() * (boxHeight + verticalSpacing);
            int startY = (getHeight() - tournyHeight / 2);
            
            for(int i = 0; i < duels.size(); i++){
                
                int y = startY + i * (boxHeight - verticalSpacing);
                
                Duel match = duels.get(i);
                
                graphics.drawRect(boxX, y, boxWidth, boxHeight);
                
                graphics.drawString(match.teamAName, boxX + 10, y + 20);
                graphics.drawString(match.teamBName, boxX + 10, y + 40);
                
                if (round < numOfRounds){
                    
                    int midY = y + boxHeight / 2;
                    int nextBox = centre + side * (round + 1) * horizontalSpacing;
                    
                    graphics.drawLine(boxX + (isLeft ? 0 : boxWidth), midY, nextBox + (isLeft ? boxWidth : 0), midY);
                    
                }
            }
        }
           
            
    }
        
}