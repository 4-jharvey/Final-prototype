import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class BracketPanel extends JPanel{
    private int tournamentID;
    private Map<Integer, List<Duel>> rounds = new HashMap <>();
    
    public BracketPanel(int tournamentID){
        this.tournamentID = tournamentID;
        Color Background = new Color(255,255,255);
        setBackground(Background);
        loadDuels();
    }
    
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
        try(Connection connect = DatabaseConnection.getConnection()){
            String query = "SELECT Duel.MatchID, Duel.Round, Team.TeamName, Duel.Winner "
                           + "FROM Duel "
                           + "JOIN Team ON Duel.TeamA = Team.TeamID OR Duel.TeamB = Team.TeamID "
                           + "WHERE Duel.TournamentID = ? "
                           + "ORDER BY Duel.Round ASC";
            PreparedStatement ps = connect.prepareStatement(query);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                String teamName = rs.getString("TeamName");
                boolean winner = rs.getBoolean("Winner");
                int round = rs.getInt("Round");

                List<Duel> listOfMatches = rounds.get(round);
                if(listOfMatches == null){
                    listOfMatches = new ArrayList<>();
                    rounds.put(round, listOfMatches);
                }
                listOfMatches.add(new Duel(teamName, winner, round));
                
            }
                
            
        } catch (SQLException ex) {
            System.getLogger(BracketPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    @Override
    protected void paintComponents(Graphics graphics){
        super.paintComponents(graphics);
        drawBracket((Graphics2D) graphics);
    }
    
    
    private void drawBracket(Graphics2D graphics){
       
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int NumOfRounds = rounds.size();
        int boxWidth = 140;
        int boxHeight = 40;
        int horizontalSpacing = 140;
        int verticalSpacing = 30;
        
        int finalDuel = panelWidth / 2;
        int leftSide = finalDuel - (NumOfRounds * horizontalSpacing);
        int rightSide = finalDuel + horizontalSpacing;
        
        drawSide(graphics, leftSide, panelHeight, boxWidth, boxHeight, horizontalSpacing, verticalSpacing, true);
        drawSide(graphics, rightSide, panelHeight, boxWidth, boxHeight, horizontalSpacing, verticalSpacing, false);
        
        graphics.drawRect(finalDuel - boxWidth / 2, panelHeight / 2 - boxHeight / 2, boxWidth, boxHeight);   
    }
    
    private void drawSide(Graphics2D graphics, int sideStart, int panelHeight, int boxWidth, int boxHeight, int horizontalSpacing, int verticalSpacing, boolean isLeft){
        
        
        Color boxBorder = new Color(0, 0, 0);
        Color textColour = new Color(0, 0, 0);
        Color connector = new Color(0, 0, 0);
        int NumOfRounds = rounds.size();
        
        for(int round = 1; round <= NumOfRounds; round++){
            List<Duel> Duels = rounds.get(round);
            if(Duels == null)continue;
            
            int spacingBetweenDuels = (int)(panelHeight / (Duels.size() + 1));
            int startX = sideStart + (round - 1) * horizontalSpacing;
                    
            for(int i = 0; i < Duels.size(); i++){
                int y = spacingBetweenDuels * (i + 1);
                Duel duel = Duels.get(i);
                
                graphics.setColor(boxBorder);
                graphics.drawRect(startX, y, boxWidth, boxHeight);
                
                graphics.setColor(textColour);
                graphics.drawString(duel.teamName, startX + 10, y + 25);
                
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