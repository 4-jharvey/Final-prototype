import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.Map;

public class BracketPanel extends JPanel {

    public static class Match {
        public int matchID;
        public String teamA;
        public String teamB;
        public String winner;
        public int round;
        public String whichBracket;

        public Match(String teamA, String teamB, String winner, int round, String whichBracket, int matchID) {
            this.matchID = matchID;
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
                    + "TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, Duel.Winner, Duel.whichBracket " 
                    + "FROM Duel " 
                    + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID " 
                    + "JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID " 
                    + "WHERE Duel.TournamentID = ? "
                    + "ORDER BY Duel.whichBracket ASC, Duel.Round ASC, Duel.MatchID ASC";

            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int matchID = rs.getInt("MatchID");
                String teamA = rs.getString("TeamAName");
                String teamB = rs.getString("TeamBName");

                String winnerID = rs.getString("Winner");
                String teamAID = rs.getString("TeamAID");

                String winner = (winnerID != null && winnerID.equals(teamAID)) ? teamA : teamB;
                
                String whichBracket = rs.getString("whichBracket");
                int round = rs.getInt("Round");

                matchList.add(new Match(teamA, teamB, winner, round, whichBracket, matchID));
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
            int verticalSpacing = 60;
            
            int startX = 40;
            int startY = 80;
            
            int winnerStartX = startX;
            drawBracket(graphic2, winnerRounds, winnerStartX, startY, boxWidth, boxHeight, horizontalSpacing, verticalSpacing);
            
            int winnerColumns = Math.max(1, winnerRounds.keySet().size());
            int loserStartX = startX + winnerColumns * horizontalSpacing + 700;
            drawBracket(graphic2, loserRounds, loserStartX, startY, boxWidth, boxHeight, horizontalSpacing, verticalSpacing);
            
            drawFinale(graphic2, finale, startX, boxWidth, boxHeight, verticalSpacing, horizontalSpacing);
        }
        
        private void drawBracket(Graphics2D graphic2, Map<Integer, List<Match>> rounds, int startX, int startY, int boxWidth, int boxHeight, int horizontalSpacing, int verticalSpacing){
            
            List<Integer> roundList = new ArrayList<>(rounds.keySet());
            Collections.sort(roundList);
            
            Map<Integer, List<Integer>> matchCentre = new HashMap<>();
            Map<Integer, Integer> roundX = new HashMap<>();

                for (int roundIndex = 0; roundIndex < roundList.size(); roundIndex++) {
                    int roundKey = roundList.get(roundIndex);
                    List<Match> matchRound = rounds.get(roundKey);
                    int xStartPoint = startX + roundIndex * horizontalSpacing;
                    
                    if (matchRound == null || matchRound.isEmpty()){
                        matchCentre.put(roundKey, new ArrayList<>());
                        roundX.put(roundKey, startX + roundIndex * horizontalSpacing);
                        continue;
                    }
                    
                    matchRound.sort(Comparator.comparingInt(match -> match.matchID));
                    
                    List<Integer> centres = new ArrayList<>();                    
                    roundX.put(roundKey, xStartPoint);
                    
                    for(int i = 0; i < matchRound.size(); i++){
                        Match match = matchRound.get(i);
                        int y;
                        int centreY;
                                                
                        if(roundIndex > 0){
                            int indexA = i * 2;
                            int indexB = i * 2 + 1;
                            
                            List<Integer> prevCentre = matchCentre.get(roundList.get(roundIndex - 1));
                            
                            if(prevCentre != null && indexA < prevCentre.size() && indexB < prevCentre.size()){
                                int indexAY = prevCentre.get(indexA);
                                int indexBY = prevCentre.get(indexB);
                                centreY = (indexAY + indexBY) / 2;
                                y = centreY - boxHeight;
                                 
                            } else{
                                y = startY + i * (boxHeight * 2 + verticalSpacing);
                                centreY = y + boxHeight;
                            }
                        } else{
                            y = startY + i * (boxHeight * 2 + verticalSpacing);
                            centreY = y + boxHeight;
                        }
                        
                        centres.add(centreY);

                        graphic2.drawRect(xStartPoint, y, boxWidth, boxHeight);
                        graphic2.drawString(match.teamA, xStartPoint + 5, y + 18);

                        graphic2.drawRect(xStartPoint, y + boxHeight, boxWidth, boxHeight);
                        graphic2.drawString(match.teamB, xStartPoint + 5, y + boxHeight + 18);
                    }
                    matchCentre.put(roundKey, centres); 
                }
                
                for(int roundIndex = 1; roundIndex < roundList.size(); roundIndex++){
                    int prevRound = roundList.get(roundIndex - 1);
                    int currentRound = roundList.get(roundIndex);
                    
                    List<Integer> prevCentre = matchCentre.get(prevRound);
                    List<Integer> currentCentre = matchCentre.get(currentRound);
                    if(prevCentre == null || currentCentre == null)
                        continue;
                    
                    int prevLeft = roundX.get(prevRound);
                    int currentLeft = roundX.get(currentRound);
                        
                    for(int i = 0; i < currentCentre.size(); i++){
                        int nextY = currentCentre.get(i);
                        int indexA = i * 2;
                        int indexB = i * 2 + 1;
                            
                        if(indexA >= 0 && indexA < prevCentre.size()){
                            drawConnector(graphic2, prevCentre.get(indexA), prevLeft, currentLeft, nextY, boxWidth);
                        }
                        if(indexB >= 0 && indexB < prevCentre.size()){
                            drawConnector(graphic2, prevCentre.get(indexB), prevLeft, currentLeft, nextY, boxWidth);
                        }
                    }
                    
                }                 
        }

        private void drawFinale(Graphics2D graphic2, List<Match> finale, int startX, int boxWidth, int boxHeight, int verticalSpacing, int horizontalSpacing){
            
            if(finale.isEmpty()){
                return;
            }
            
            int screenWidth = getWidth();
            int centreX = screenWidth / 2 - boxWidth / 2;
            int centreY = getHeight() - 220;
            
            for (Match finals : finale){
                
                if(finals.teamA == null && finals.teamB == null)
                    continue;
                
                graphic2.drawRect(centreX, centreY, boxWidth, boxHeight);
                graphic2.drawString(finals.teamA, centreX + 5, centreY + 18);
                
                graphic2.drawRect(centreX, centreY + boxHeight, boxWidth, boxHeight);
                graphic2.drawString(finals.teamB, centreX + 5, centreY + boxHeight + 18);
                
                if(finals.winner != null && !finals.winner.isEmpty()){
                    graphic2.drawString("Winner " + finals.winner, centreX + 5, centreY + boxHeight * 2 + 18);
                }
            }
            
        }
        
        private void drawConnector(Graphics2D graphic2, int prevY, int prevLeft, int currentLeft, int nextY, int boxWidth) {
            
            
            int prevRX = prevLeft + boxWidth;
            int midX = (prevRX + currentLeft) / 2;
            
            graphic2.drawLine(prevRX, prevY, midX, prevY);
            graphic2.drawLine(midX, prevY, midX, nextY);
            graphic2.drawLine(midX, nextY, currentLeft, nextY);
            
            System.out.printf("Connector prevRightX=%d → midX=%d → currLeftX=%d, prevY=%d → nextY=%d%n", prevRX, midX, currentLeft, prevY, nextY);



        }
        
        @Override
        public Dimension getPreferredSize() {
            int boxHeight = 30;
            int verticalSpacing = 60;
            int horizontalSpacing = 150;
            
            int roundsMax = winnerRounds.keySet().size() + loserRounds.keySet().size() + (finale.isEmpty() ? 0 : 1);
            int matchesMax = getmatchesPerRound();
            
            int width = roundsMax * horizontalSpacing + 300;
            int height = matchesMax + (boxHeight * 2 + verticalSpacing) + 200;
            return new Dimension(1300, 800);
        }
        
        private int getmatchesPerRound(){
            int maximum = 0;
            
            for(List<Match> matches : winnerRounds.values()){
                maximum = Math.max(maximum, matches.size());
            }
            for(List<Match> matches : loserRounds.values()){
                maximum = Math.max(maximum, matches.size());
            }
            if(finale.isEmpty()){
                maximum = Math.max(maximum, finale.size());
            }
                
            
            return maximum;
        }
            
    
    }


    public static JPanel getBracketPanel(int tournamentID) {

        BracketPanel tempPanel = new BracketPanel();
        java.util.List<Match> matches = tempPanel.loadMatches(tournamentID);
        return new BracketPane(matches);
    }

    public BracketPanel() {}
}
