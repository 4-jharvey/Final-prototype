import java.awt.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.JFrame;

public class Duel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Duel.class.getName());

    private int tournamentID;
    private int matchID;
    
    public Duel(int tournamentID) {
        this.tournamentID = tournamentID;
        initComponents();
        //JFrame fills the screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        currentMatch();
    }
    
    private void currentMatch(){
        try(Connection connect = DatabaseConnection.getConnection()){
            
            String sql = "SELECT Duel.MatchID, TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, Schedule.Time FROM Duel "
                        + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID "
                        + "LEFT JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID "
                        + "JOIN Schedule ON Duel.MatchID = Schedule.matchID "
                        + "WHERE Duel.TournamentID = ? "
                        + "ORDER BY Schedule.Time ASC LIMIT 1";
            PreparedStatement match = connect.prepareStatement(sql);
            match.setInt(1, tournamentID);
            ResultSet rsMatch = match.executeQuery();
            
            if(rsMatch.next()){
                matchID = rsMatch.getInt("MatchID");
                TeamA.setText(rsMatch.getString("TeamAName"));
                TeamB.setText(rsMatch.getString("TeamBName"));
                
                String teamAName = rsMatch.getString("TeamAName");
                String teamBName = rsMatch.getString("TeamBName");
                
                TeamA.setEditable(false);
                TeamB.setEditable(false);

                AddPointA.setText("Add point to " + teamAName);
                AddPointB.setText("Add point to " + teamBName);
                
                teamStats(connect, teamAName, TeamAStats);
                teamStats(connect, teamBName, TeamBStats);
                
            }
            
        } catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
    }
    
    private void teamStats(Connection connect, String team, JTextArea statsDisplayer){
        try{
            String statsQuery = "SELECT player.Username, Player_stats.Kills, Player_stats.Deaths, Player_stats.Assists, Player_stats.Wins, Player_stats.Losses "
                                + "FROM Player_stats "
                                + "JOIN Player player ON Player_stats.PlayerID = player.PlayerID "
                                + "JOIN Team team ON player.TeamID = team.TeamID "
                                + "WHERE team.TeamName = ?";
            PreparedStatement psStats = connect.prepareStatement(statsQuery);
            psStats.setString(1, team);
            ResultSet rsStats = psStats.executeQuery();
            
            int teamKills = 0;
            int teamDeaths = 0;
            int teamAssists = 0;
            int teamWins = 0;
            int teamLosses = 0;
            int count = 0;
            
            while (rsStats.next()){
                int kills = rsStats.getInt("Kills");
                int deaths = rsStats.getInt("Deaths");
                int assists = rsStats.getInt("Assists");
                int wins = rsStats.getInt("Wins");
                int losses = rsStats.getInt("Losses");
                
                count++;
                
                teamKills = kills + teamKills;
                teamDeaths = deaths + teamDeaths;
                teamAssists = assists + teamAssists;
                teamWins = wins + teamWins;
                teamLosses = losses + teamLosses;
                
            }
            if(count > 0){
                statsDisplayer.append("Team Statistics: "
                                     + "Average Kills: " + teamKills/count
                                     + "Average Deaths: " + teamDeaths/count
                                     + "Average Assists: " + teamAssists/count
                                     + "Average Wins: " + teamWins/count
                                     + "Average Losse: " + teamLosses/count);
            }  else {
                statsDisplayer.append("No Data about " + team + " at this current moment");
            }
        } catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BackToBracket = new javax.swing.JButton();
        AddPointB = new javax.swing.JButton();
        AddPointA = new javax.swing.JButton();
        TeamA = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TeamBStats = new javax.swing.JTextArea();
        TeamB = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        TeamAStats = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BackToBracket.setText("Back to Bracket");
        BackToBracket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackToBracketActionPerformed(evt);
            }
        });
        getContentPane().add(BackToBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 520, 280, 40));

        AddPointB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddPointBActionPerformed(evt);
            }
        });
        getContentPane().add(AddPointB, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 450, 240, 40));

        AddPointA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddPointAActionPerformed(evt);
            }
        });
        getContentPane().add(AddPointA, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 240, 40));

        TeamA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TeamAActionPerformed(evt);
            }
        });
        getContentPane().add(TeamA, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 240, 50));

        TeamBStats.setColumns(20);
        TeamBStats.setRows(5);
        jScrollPane1.setViewportView(TeamBStats);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 130, 240, 300));

        TeamB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TeamBActionPerformed(evt);
            }
        });
        getContentPane().add(TeamB, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 50, 240, 50));

        TeamAStats.setColumns(20);
        TeamAStats.setRows(5);
        jScrollPane2.setViewportView(TeamAStats);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 240, 300));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BackToBracketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackToBracketActionPerformed
        //takes user back to the tournament screen
        Tournament Tourny = new Tournament(tournamentID);
        this.dispose();
        Tourny.setVisible(true);
    }//GEN-LAST:event_BackToBracketActionPerformed

    private void AddPointBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddPointBActionPerformed

        
        try(Connection connect = DatabaseConnection.getConnection()){
            
            String sql = "UPDATE Duel SET TeamB_Score = TeamB_Score + 1 WHERE TournamentID = ? AND MatchID = ?";
            PreparedStatement psScoreB = connect.prepareStatement(sql);
            psScoreB.setInt(1, tournamentID);
            psScoreB.setInt(2, matchID);
            psScoreB.executeUpdate();
        } catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
    }//GEN-LAST:event_AddPointBActionPerformed

    private void AddPointAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddPointAActionPerformed

        
        try(Connection connect = DatabaseConnection.getConnection()){
            
            String sql = "UPDATE Duel SET TeamA_Score = TeamA_Score + 1  WHERE TournamentID = ? AND MatchID = ?";
            PreparedStatement psScore = connect.prepareStatement(sql);
            psScore.setInt(1, tournamentID);
            psScore.setInt(2, matchID);
            psScore.executeUpdate();
        } catch (SQLException ex) {
            System.getLogger(BracketGenerator.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
    }//GEN-LAST:event_AddPointAActionPerformed

    private void TeamAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TeamAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TeamAActionPerformed

    private void TeamBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TeamBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TeamBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddPointA;
    private javax.swing.JButton AddPointB;
    private javax.swing.JButton BackToBracket;
    private javax.swing.JTextField TeamA;
    private javax.swing.JTextArea TeamAStats;
    private javax.swing.JTextField TeamB;
    private javax.swing.JTextArea TeamBStats;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
