import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;


public class BracketPanel extends JPanel{

   public class Match{
       public String teamA;
       public String teamB;
       public String winner;
       public int round;
       
       public Match(String teamA, String teamB, String winner, int round){
           this.teamA = teamA;
           this.teamB = teamB;
           this.winner = winner;
           this.round = round;
       }
   }


    public java.util.List<Match> loadMatches(int tournamentID){
        java.util.List<Match> matchList = new java.util.ArrayList<>();
        
        try(Connection connect = DatabaseConnection.getConnection()){
            String sql = "SELECT Duel.MatchID, Duel.Round, Duel.TeamA AS TeamAID, Duel.TeamB AS TeamBID, TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, Duel.Winner, Duel.Round "
                           + "FROM Duel "
                           + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID "
                           +" JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID "
                           + "WHERE Duel.TournamentID = ? "
                           + "ORDER BY Duel.Round ASC";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                 String teamA = rs.getString("TeamAName");
                 String teamB = rs.getString("TeamBName");
                 String winnerID = rs.getString("Winner");
                 String teamAID = rs.getString("TeamAID");
                 
                 String winner = (winnerID == teamAID) ? teamA : teamB;
                 
                 matchList.add(new Match(teamA, teamB, winner, rs.getInt("Round")));
                     
            }
            
            
        }catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.getMessage());
        }
        
        return matchList;
    }
    
    public class BracketPane extends JPanel{
        private final java.util.Map<Integer, java.util.List<Match>> rounds = new java.util.HashMap<>();
        
        public BracketPane(java.util.List<Match> matches){
            for(Match match : matches){
                java.util.List<Match> roundList = rounds.getInt(match.round);
                if(roundList == null){
                   roundList = new ArrayList<>();
                   rounds.put(match.round, roundList);
                   
                   
                }
                
                roundList.add(match);
            }
        }
        
        
        @Override
        protected void paintComponent(Graphics graphic){
            super.paintComponent(graphic);
            
            Graphics2D graphic2 = (Graphics2D) graphic;
            
            int roundCount = 0;
            int boxWidth = 140;
            int boxHeight = 30;
            int spacingHorizontal = 150;
            int spacingVertical = 40;
            
            for (int round : new java.util.TreeSet<>(rounds.keySet())){
                java.util.List<Match> matchRound = rounds.get(round);
                
                int xStartPoint = 40 + roundCount * spacingHorizontal;
                int yStartPoint = 40;
                
                for(int i = 0; i < matchRound.size(); i++){
                    Match match = matchRound.get(i);
                    int startY = yStartPoint + i * (boxHeight * 2 + spacingVertical);
                    
                    graphic2.drawRect(xStartPoint, startY, boxWidth, boxHeight);
                    graphic2.drawString(match.teamA, xStartPoint + 5, startY + 10);
                    
                    graphic2.drawRect(xStartPoint, startY + boxHeight, boxWidth, boxHeight);
                    graphic2.drawString(match.teamB, xStartPoint + 5, startY + 10);
                    
                    drawConnector(graphic2, round, i, xStartPoint, startY, boxWidth, boxHeight, spacingVertical, spacingHorizontal);
                    
                }
                
                roundCount++;
               
            }
                  
        }
    }
    
        private void drawConnector(Graphics2D graphic2, int round, int index, int xStartPoint, int startY, int boxWidth, int boxHeight, int spacingVertical, int spacingHorizontal){
            if(!rounds.containsKey(round + 1))
                    return;
                
                int nextX = xStartPoint + spacingHorizontal;
                int midY = startY + boxHeight;
                int nextIndex = index / 2;
                int nextY = 40 + nextIndex * (boxHeight * 2 + spacingVertical) + boxHeight;
               
                graphic2.drawLine(nextX + boxWidth, midY, nextX, nextY);
            }
            
        
            public Dimension getPreferedSize(){
                return new Dimension(1300, 800);
            } 
    
    public static JPanel getBracketPanel(int tournamentID){
        java.util.List<Match> matches = loadMatches(tournamentID);
        return new BracketPanel(matches);
    }
}
