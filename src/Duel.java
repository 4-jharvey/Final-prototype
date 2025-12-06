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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
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
        int ScoreB = 0;
        
        try(Connection connect = DatabaseConnection.getConnection()){
            ScoreB++;
            
            String sql = "INSERT INTO Duel(TeamB_Score) VALUES (?) WHERE TournamentID = ? AND MatchID = ?";
            PreparedStatement psScoreB = connect.prepareStatement(sql);
            psScoreB.setInt(1, ScoreB);
            psScoreB.setInt(2, tournamentID);
            psScoreB.setInt(3, matchID);
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
        int scoreA = 0;
        
        try(Connection connect = DatabaseConnection.getConnection()){
            scoreA++;
            
            String sql = "INSERT INTO Duel(TeamA_Score) VALUES (?) WHERE TournamentID = ? AND MatchID = ?";
            PreparedStatement psScore = connect.prepareStatement(sql);
            psScore.setInt(1, scoreA);
            psScore.setInt(2, tournamentID);
            psScore.setInt(3, matchID);
           
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
