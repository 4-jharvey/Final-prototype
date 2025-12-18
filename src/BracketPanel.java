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
        

        public Match(String teamA, String teamB, String winner, int round, int matchID) {
            this.matchID = matchID;
            this.teamA = teamA;
            this.teamB = teamB;
            this.winner = winner;
            this.round = round;
            
            
            
        }
    }

    
    //method that loads the matches made in the bracket generator to this java class
    public List<Match> loadMatches(int tournamentID) {
        List<Match> matchList = new ArrayList<>();
        
        //selects the necessary data
        try (Connection connect = DatabaseConnection.getConnection()) {
            String sql = "SELECT Duel.MatchID, Duel.Round, Duel.TeamA AS TeamAID, Duel.TeamB AS TeamBID, "
                        + "TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, Duel.Winner " 
                        + "FROM Duel " 
                        + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID " 
                        + "LEFT JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID " 
                        + "WHERE Duel.TournamentID = ? "
                        + "ORDER BY Duel.Round ASC, Duel.MatchID ASC";

            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, tournamentID);
            ResultSet rs = ps.executeQuery();
            
            //turns the data selected into variables in the method
            while (rs.next()) {
                int matchID = rs.getInt("MatchID");
                String teamA = rs.getString("TeamAName");
                String teamB = rs.getString("TeamBName");
                
                //in case of odd team
                if(teamB == null){
                    teamB = "";                    
                }
                
                //gives the recieved data is turned into variables
                String winnerID = rs.getString("Winner");
                String teamAID = rs.getString("TeamAID");
                String teamBID = rs.getString("TeamBID");
                
                //selects the winner depending on who the winner Id matches to
                String winner = " ";
                if(winnerID == teamAID){
                    winner = teamAID;
                } else if (winnerID == teamBID){
                    winner = teamBID;
                    
                }
                
                
                int round = rs.getInt("Round");
                
                //add data to the list
                matchList.add(new Match(teamA, teamB, winner, round, matchID));
            }
            
            //debugging statement
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL Error: " + ex.toString());
        }

        return matchList;
    }

    // this is used to draw the bracket
    public static class BracketPane extends JPanel {
        
        private final Map<Integer, List<Match>> winnerRounds = new HashMap<>();
        private final List<Match> finale = new ArrayList<>();
        
        public BracketPane(List<Match> matches) {
            
            //loops for every match in the list
            for (Match match : matches) {
                //in case it is the finale round
                if(match.round == 0 || match.round == 100){                      
                        finale.add(match);
                        
                }
                else{
                    //finds out how many matches there will be per round
                    List<Match> matchesPerRound = winnerRounds.get(match.round);
                    if(matchesPerRound == null){
                        matchesPerRound = new ArrayList<>();
                        winnerRounds.put(match.round, matchesPerRound);
                    }
                        //adds the matches to new list
                        matchesPerRound.add(match);
 
                }
                
            }
        }

        //this is what starts to draw the visual bracket
        @Override
        protected void paintComponent(Graphics graphic) {
            super.paintComponent(graphic);
            Graphics2D graphic2 = (Graphics2D) graphic;
            
            //size of each box and spacing between boxes
            int boxWidth = 140;
            int boxHeight = 30;
            int horizontalSpacing = 150;
            int verticalSpacing = 60;
            
            //starting X and Y point
            int startX = 40;
            int startY = 80;
            
            int maxMatches = 1;
            // finds out how many rounds there will be in the bracket
            for(List<Match> roundList : winnerRounds.values()){
                int size = roundList.size();
                if(size > maxMatches){
                    maxMatches = size;
                }
            
            }
            
            //draws the bracket
            drawBracket(graphic2, winnerRounds, startX, startY, boxWidth, boxHeight, horizontalSpacing, verticalSpacing);
            
            //draws the final box that shows the final winner
            drawFinale(graphic2, finale, startX, boxWidth, boxHeight, verticalSpacing, horizontalSpacing);
            
        }
        
        //method draws the bracket
        private void drawBracket(Graphics2D graphic2, Map<Integer, List<Match>> rounds, int startX, int startY, int boxWidth, int boxHeight, int horizontalSpacing, int verticalSpacing){
            // gets the all of the rounds and sorts it
            List<Integer> roundList = new ArrayList<>(rounds.keySet());
            Collections.sort(roundList);
            
            //stores the vertical positions of matches
            Map<Integer, List<Integer>> matchCentre = new HashMap<>();
            //stores x co-ordinate of each box
            Map<Integer, Integer> roundX = new HashMap<>();
                //loops the code for each round
                for (int roundIndex = 0; roundIndex < roundList.size(); roundIndex++) {
                    int roundKey = roundList.get(roundIndex);
                    List<Match> matchRound = rounds.get(roundKey);
                    int xStartPoint = startX + roundIndex * horizontalSpacing;
                    
                    //if no matches exist in round go to next
                    if (matchRound == null || matchRound.isEmpty()){
                        matchCentre.put(roundKey, new ArrayList<>());
                        roundX.put(roundKey, startX + roundIndex * horizontalSpacing);
                        continue;
                    }
                    
                    //sorts matches in round by matchID
                    matchRound.sort(Comparator.comparingInt(match -> match.matchID));
                    
                    //holds vertical position of matches in specific round
                    List<Integer> centres = new ArrayList<>();                    
                    roundX.put(roundKey, xStartPoint);
                    //loops through matches in round
                    for(int i = 0; i < matchRound.size(); i++){
                        Match match = matchRound.get(i);
                        int y; //top of the match box
                        int centreY; // vertical centre of the match box
                        //after first rounds matches are aligned based on previous centres
                        if(roundIndex > 0){
                            int indexA = i * 2;
                            int indexB = i * 2 + 1;
                            
                            List<Integer> prevCentre = matchCentre.get(roundList.get(roundIndex - 1));
                            //makes sure both matches exist
                            if(prevCentre != null && indexA < prevCentre.size() && indexB < prevCentre.size()){
                                //centre match between them
                                int indexAY = prevCentre.get(indexA);
                                int indexBY = prevCentre.get(indexB);
                                centreY = (indexAY + indexBY) / 2;
                                y = centreY - boxHeight;
                                 //if not it is placed sequentially + spacing
                            } else{
                                y = startY + i * (boxHeight * 2 + verticalSpacing);
                                centreY = y + boxHeight;
                            }
                        } else{
                            //this is when it is the first round, also places them sequentially with spacing
                            y = startY + i * (boxHeight * 2 + verticalSpacing);
                            centreY = y + boxHeight;
                        }
                        
                        //draws boxes
                        if(match.teamB == null || match.teamB.isEmpty()){
                            //this is for when there is an odd number of teams
                            graphic2.drawRect(xStartPoint, y, boxWidth, boxHeight);
                            graphic2.drawString(match.teamA, xStartPoint + 5, y + 18);
                            // centre is halfway through the box
                            centreY = y + boxHeight / 2;
                            
                        }else{
                            //draws for when there are two teams
                            graphic2.drawRect(xStartPoint, y, boxWidth, boxHeight);
                            graphic2.drawString(match.teamA, xStartPoint + 5, y + 18);

                            graphic2.drawRect(xStartPoint, y + boxHeight, boxWidth, boxHeight);
                            graphic2.drawString(match.teamB, xStartPoint + 5, y + boxHeight + 18);
                            
                            //centre between boxes
                            centreY = y + boxHeight;
                        }
                        //stores the vertical for the match
                        centres.add(centreY);
                    }
                    //saves all centres for the matches
                    matchCentre.put(roundKey, centres); 
                }
                //this draws the connectors between boxes
                for(int roundIndex = 1; roundIndex < roundList.size(); roundIndex++){
                    int prevRound = roundList.get(roundIndex - 1);
                    int currentRound = roundList.get(roundIndex);
                    
                    List<Integer> prevCentre = matchCentre.get(prevRound);
                    List<Integer> currentCentre = matchCentre.get(currentRound);
                    //in case there are no centres
                    if(prevCentre == null || currentCentre == null)
                        continue;
                    
                    int prevLeft = roundX.get(prevRound);
                    int currentLeft = roundX.get(currentRound);
                    //connects every current match to its previous match
                    for(int i = 0; i < currentCentre.size(); i++){
                        int nextY = currentCentre.get(i);
                        int indexA = i * 2;
                        int indexB = i * 2 + 1;
                        //connect previous match A to current match
                        if(indexA >= 0 && indexA < prevCentre.size()){
                            drawConnector(graphic2, prevCentre.get(indexA), prevLeft, currentLeft, nextY, boxWidth);
                        }//connect previous match B to current match
                        if(indexB >= 0 && indexB < prevCentre.size()){
                            drawConnector(graphic2, prevCentre.get(indexB), prevLeft, currentLeft, nextY, boxWidth);
                        }
                    }
                    
                }                  
        }
        //draws the finale box
        private void drawFinale(Graphics2D graphic2, List<Match> finale, int startX, int boxWidth, int boxHeight, int verticalSpacing, int horizontalSpacing){
            
            //if there is no final winner yet
            if(finale.isEmpty()){
                return;
            }
            
            //gets the panel's measurements
            int screenWidth = getWidth();
            int centreX = screenWidth / 2 - boxWidth / 2;
            int centreY = (getPreferredSize().height / 2) - 700;
            
            //loops through the code in case the final winner is changed
            for (Match finals : finale){
                
                //in case there is no final winner
                if(finals.winner == null || finals.winner.isEmpty())
                    continue;                
                
                //draws the finale box with team name inside
                graphic2.drawRect(centreX, centreY + boxHeight, boxWidth, boxHeight);
                graphic2.drawString("Winner: " + finals.winner, centreX + 5, centreY + boxHeight + 18);
                
            }
            
        }
        // draw connector lines between boxes
        private void drawConnector(Graphics2D graphic2, int prevY, int prevLeft, int currentLeft, int nextY, int boxWidth) {
            
            //obtains X values needed to draw connnectors
            int prevRX = prevLeft + boxWidth;
            int midX = (prevRX + currentLeft) / 2;
            
            //draws the lines for the connector
            graphic2.drawLine(prevRX, prevY, midX, prevY);
            graphic2.drawLine(midX, prevY, midX, nextY);
            graphic2.drawLine(midX, nextY, currentLeft, nextY);
            
            // debugging statement to make sure connectors go to the right place
            System.out.printf("Connector prevRightX to midX to currLeftX, prevY to nextY", prevRX, midX, currentLeft, prevY, nextY);



        }
        
        //gets the size for the panel to be
        @Override
        public Dimension getPreferredSize() {
            //dimensions for boxes
            int boxHeight = 30;
            int verticalSpacing = 60;
            int horizontalSpacing = 150;
            
            //amount of rounds and matches that will happen
            int roundsMax = winnerRounds.keySet().size() + (finale.isEmpty() ? 0 : 1);
            int matchesMax = getmatchesPerRound();
            
            //gets the height and width box should be
            int width = roundsMax * horizontalSpacing + 300;
            int height = matchesMax * (boxHeight + verticalSpacing) + 200;
            
            //debugging statement
            System.out.println("Height = " + height);
            
            //Dimensions, hardcoded height as height value was not working no matter what
            return new Dimension(width, 2000);
        }
        
        //get the amount of matches per round
        private int getmatchesPerRound(){
            int maximum = 0;
            //for the barcket
            for(List<Match> matches : winnerRounds.values()){
                maximum = Math.max(maximum, matches.size());
            }
            //for the finale box
            if(finale.isEmpty()){
                maximum = Math.max(maximum, finale.size());
            }
                
            
            return maximum;
        }
            
    
    }

    //creates the panel that holds the bracket in
    public static JPanel getBracketPanel(int tournamentID) {

        BracketPanel tempPanel = new BracketPanel();
        java.util.List<Match> matches = tempPanel.loadMatches(tournamentID);
        return new BracketPane(matches);
    }
    
    //so the panel an be called upon
    public BracketPanel() {}
}
