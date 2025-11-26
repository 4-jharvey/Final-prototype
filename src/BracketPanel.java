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
        String teamName;
        boolean winner;
        int round;
        
        Duel(String teamName, boolean winner, int round){
            this.teamName = teamName;
            this.winner = winner;
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
                listOfMatches.add(new Duel(teamAName, aWins, round));
                listOfMatches.add(new Duel(teamBName, bWins, round));
                
            }
                
            // catches any errors
        } catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + "Type: " + ex.getClass().getName() + "\n"+ "Message: " + ex.getMessage());
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
        int NumOfRounds = rounds.size();
        int boxWidth = 140;
        int boxHeight = 40;
        int horizontalSpacing = 140;
        int verticalSpacing = 30;
        
        //the final duel box and spacing for each side of the bracket
        int finalDuel = panelWidth / 2;
        int leftSide = finalDuel - (NumOfRounds * horizontalSpacing);
        int rightSide = finalDuel + horizontalSpacing;
        
        //each side of the bracket
        drawSide(graphics, leftSide, panelHeight, boxWidth, boxHeight, horizontalSpacing, verticalSpacing, true);
        drawSide(graphics, rightSide, panelHeight, boxWidth, boxHeight, horizontalSpacing, verticalSpacing, false);
        
        //each box
        graphics.drawRect(finalDuel - boxWidth / 2, panelHeight / 2 - boxHeight / 2, boxWidth, boxHeight);   
    }
    // the class that draws each side
    private void drawSide(Graphics2D graphics, int sideStart, int panelHeight, int boxWidth, int boxHeight, int horizontalSpacing, int verticalSpacing, boolean isLeft){
        
        //customizable colour scheme
        Color boxBorder = new Color(0, 0, 0);
        Color textColour = new Color(0, 0, 0);
        Color connector = new Color(0, 0, 0);
        int NumOfRounds = rounds.size();
        
        //iterates through the list to create each box, connector lines and the team names in the boxes
        for(int round = 1; round <= NumOfRounds; round++){
            java.util.List<Duel> Duels = rounds.get(round);
            if(Duels == null)continue;
            //where to start painting from
            int spacingBetweenDuels = (int)(panelHeight / (Duels.size() + 1));
            int startX = sideStart + (round - 1) * horizontalSpacing;
            //draws the each box
            for(int i = 0; i < Duels.size(); i++){
                int y = spacingBetweenDuels * (i + 1);
                Duel duel = Duels.get(i);
                
                graphics.setColor(boxBorder);
                graphics.drawRect(startX, y, boxWidth, boxHeight);
                
                graphics.setColor(textColour);
                graphics.drawString(duel.teamName, startX + 10, y + 25);
                //this makes sure all connector lines are connected to each box and there are no extras
                if(round < NumOfRounds){
                    int nextX = isLeft ? startX + horizontalSpacing : startX - horizontalSpacing;
                    int nextY = (int)(y + boxHeight / 2);
                    graphics.setColor(connector);
                    graphics.drawLine(startX + (isLeft ? boxWidth : 0), nextY, nextX, nextY);
                }
            }
        }
            
    }
        
}