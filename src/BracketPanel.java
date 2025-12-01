import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.Map;

public class BracketPanel extends JPanel {

    public static class Match {
        public String teamA;
        public String teamB;
        public String winner;
        public int round;
        public String whichBracket;

        public Match(String teamA, String teamB, String winner, int round, String whichBracket) {
            this.teamA = teamA;
            this.teamB = teamB;
            this.winner = winner;
            this.round = round;
            this.whichBracket = whichBracket;
            
            
        }
    }

    
    
    public List<Match> loadMatches(int tournamentID) {
        List<Match> matchList = new ArrayList<>();

        try (Connection connect = DatabaseConnection.getConnection()) {
            String sql =
                    "SELECT Duel.MatchID, Duel.Round, Duel.TeamA AS TeamAID, Duel.TeamB AS TeamBID, "
                    + "TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, Duel.Winner " 
                    + "FROM Duel " 
                    + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID " 
                    + "JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID " 
                    + "WHERE Duel.TournamentID = ? "
                    + "ORDER BY Duel.whichBracket ASC, Duel.Round ASC";

            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String teamA = rs.getString("TeamAName");
                String teamB = rs.getString("TeamBName");

                String winnerID = rs.getString("Winner");
                String teamAID = rs.getString("TeamAID");

                String winner = (winnerID != null && winnerID.equals(teamAID)) ? teamA : teamB;
                
                String whichBracket = rs.getString("whichBracket");
                int round = rs.getInt("Round");

                matchList.add(new Match(teamA, teamB, winner, round, whichBracket));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL Error: " + ex);
        }

        return matchList;
    }


    public static class BracketPane extends JPanel {

        private final Map<Integer, List<Match>> winnerRounds = new HashMap<>();
        private final Map<Integer, List<Match>> loserRounds = new HashMap<>();
        private final List<Match> finale = new ArrayList<>();

        public BracketPane(List<Match> matches) {

            for (Match match : matches) {
                switch(match.whichBracket){
                    case "W": {
                        List<Match> matchesPerRound = winnerRounds.get(match.round);
                        if(matchesPerRound == null){
                            matchesPerRound = new ArrayList<>();
                            winnerRounds.put(match.round, matchesPerRound);
                        }
                        
                        matchesPerRound.add(match);
                        break;
                    }
                    
                    case "L": {
                        List<Match> matchesPerRound = loserRounds.get(match.round);
                        if(matchesPerRound == null){
                            matchesPerRound = new ArrayList<>();
                            loserRounds.put(match.round, matchesPerRound);
                            
                        }
                        
                        matchesPerRound.add(match);
                        break;
                            
                    }
                    case "F": {
                        finale.add(match);
                        break;
                    }
                    default: {
                        System.out.println("Unknown bracket appeared " + match.whichBracket);
                    }
                        
                        
                    
                }
                
            }
        }

        
        @Override
        protected void paintComponent(Graphics graphic) {
            super.paintComponent(graphic);
            Graphics2D graphic2 = (Graphics2D) graphic;

            int boxWidth = 140;
            int boxHeight = 30;
            int horizontalSpacing = 150;
            int verticalSpacing = 40;
            
            int startX = 40;
            int startY = 40;
            
            int winnerStartX = startX;
            drawBracket(graphic2, winnerRounds, winnerStartX, startX, boxWidth, boxHeight, horizontalSpacing, verticalSpacing);
            
            int winnerColumns = Math.max(1, winnerRounds.keySet().size());
            int loserStartX = startX + winnerColumns * horizontalSpacing + 300;
            drawBracket(graphic2, loserRounds, loserStartX, startX, boxWidth, boxHeight, horizontalSpacing, verticalSpacing);
            
            drawFinale(graphic2, finale, startX, boxWidth, boxHeight, verticalSpacing, horizontalSpacing);
        }
        
        private void drawBracket(Graphics2D graphic2, Map<Integer, List<Match>> rounds, int startX, int startY, int boxWidth, int boxHeight, int horizontalSpacing, int verticalSpacing){
            
            List<Integer> roundList = new ArrayList<>();
            Collections.sort(roundList);
            
            int roundCount = 0;
            
            for (int round : roundList) {

                List<Match> matchRound = rounds.get(round);

                int xStartPoint = startX + roundCount * horizontalSpacing;
                int yStartPoint = startY;

                for (int i = 0; i < matchRound.size(); i++) {
                    Match match = matchRound.get(i);
                    int y = yStartPoint + i * (boxHeight * 2 + verticalSpacing);

                    graphic2.drawRect(xStartPoint, y, boxWidth, boxHeight);
                    graphic2.drawString(match.teamA, xStartPoint + 5, y + 18);

                    graphic2.drawRect(xStartPoint, y + boxHeight, boxWidth, boxHeight);
                    graphic2.drawString(match.teamB, xStartPoint + 5, y + boxHeight + 18);

                    drawConnector(graphic2, rounds, round, i, xStartPoint, y, boxWidth, boxHeight, verticalSpacing, horizontalSpacing);
                }

                roundCount++;
            }
        }

        private void drawFinale(Graphics2D graphic2, List<Match> finale, int startX, int boxWidth, int boxHeight, int verticalSpacing, int horizontalSpacing){
            
            if(finale.isEmpty()){
                return;
            }
            
            int screenWidth = getWidth();
            int centreX = Math.max(startX + 200, screenWidth / 2 - boxWidth / 2);
            int centreY = getHeight() - 180;
            
            for (Match finals : finale){
                
                graphic2.drawRect(centreX, centreY, boxWidth, boxHeight);
                graphic2.drawString(finals.teamA, centreX + 5, centreY + 18);
                
                graphic2.drawRect(centreX, centreY + boxHeight, boxWidth, boxHeight);
                graphic2.drawString(finals.teamB, centreX + 5, centreY + 18);
            }
            
        }
        
        private void drawConnector(Graphics2D graphic2, Map<Integer, List<Match>> rounds, int round, int index, int startX, int y, int boxWidth, int boxHeight, int verticalSpacing, int horizontalSpacing) {

            if (!rounds.containsKey(round + 1))
                return;

            int nextX = startX + horizontalSpacing;
            int midY = y + boxHeight;

            int nextIndex = index / 2;
            int nextY = 40 + nextIndex * (boxHeight * 2 + verticalSpacing) + boxHeight;

            graphic2.drawLine(startX + boxWidth, midY, nextX, nextY);
            graphic2.drawLine(nextX, midY, nextX, nextY);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1300, 800);
        }
    
    }


    public static JPanel getBracketPanel(int tournamentID) {

        BracketPanel tempPanel = new BracketPanel();
        java.util.List<Match> matches = tempPanel.loadMatches(tournamentID);
        return new BracketPane(matches);
    }

    public BracketPanel() {}
}
