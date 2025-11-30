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

        public Match(String teamA, String teamB, String winner, int round) {
            this.teamA = teamA;
            this.teamB = teamB;
            this.winner = winner;
            this.round = round;
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
                    + "ORDER BY Duel.Round ASC";

            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String teamA = rs.getString("TeamAName");
                String teamB = rs.getString("TeamBName");

                String winnerID = rs.getString("Winner");
                String teamAID = rs.getString("TeamAID");

                String winner = (winnerID != null && winnerID.equals(teamAID)) ? teamA : teamB;

                matchList.add(new Match(teamA, teamB, winner, rs.getInt("Round")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL Error: " + ex);
        }

        return matchList;
    }


    public static class BracketPane extends JPanel {

        private final Map<Integer, List<Match>> rounds = new HashMap<>();

        public BracketPane(List<Match> matches) {

            for (Match match : matches) {
                List<Match> list = rounds.get(match.round);

                if (list == null) {
                    list = new ArrayList<>();
                    rounds.put(match.round, list);
                }

                list.add(match);
            }
        }

        
        @Override
        protected void paintComponent(Graphics graphic) {
            super.paintComponent(graphic);

            Graphics2D graphic2 = (Graphics2D) graphic;

            int roundCount = 0;
            int boxWidth = 140;
            int boxHeight = 30;
            int spacingHorizontal = 150;
            int spacingVertical = 40;

            for (int round : new TreeSet<>(rounds.keySet())) {

                List<Match> matchRound = rounds.get(round);

                int xStartPoint = 40 + roundCount * spacingHorizontal;
                int yStartPoint = 40;

                for (int i = 0; i < matchRound.size(); i++) {
                    Match match = matchRound.get(i);
                    int startY = yStartPoint + i * (boxHeight * 2 + spacingVertical);

                    graphic2.drawRect(xStartPoint, startY, boxWidth, boxHeight);
                    graphic2.drawString(match.teamA, xStartPoint + 5, startY + 15);

                    graphic2.drawRect(xStartPoint, startY + boxHeight, boxWidth, boxHeight);
                    graphic2.drawString(match.teamB, xStartPoint + 5, startY + boxHeight + 15);

                    drawConnector(graphic2, round, i, xStartPoint, startY, boxWidth, boxHeight, spacingVertical, spacingHorizontal);
                }

                roundCount++;
            }
        }

        
        
        private void drawConnector(Graphics2D graphic2, int round, int index, int startX, int startY, int boxWidth, int boxHeight, int spacingVertical, int spacingHorizontal) {

            if (!rounds.containsKey(round + 1))
                return;

            int nextX = startX + spacingHorizontal;
            int midY = startY + boxHeight;

            int nextIndex = index / 2;
            int nextY = 40 + nextIndex * (boxHeight * 2 + spacingVertical) + boxHeight;

            graphic2.drawLine(nextX + boxWidth, midY, nextX, nextY);
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
