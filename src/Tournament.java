import java.awt.*;
import javax.swing.*;


public class Tournament extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Tournament.class.getName());
    
    
    private int tournamentID;
    private BracketPanel bracketPanel;

    public Tournament(int tournamentID) {
        this.tournamentID = tournamentID;
        initComponents();
        
        BracketGenerator.generateBracket(tournamentID);
        
        bracketPanel = new BracketPanel(tournamentID);
        bracketPanel.setBounds(50, 30, 850, 400);
        add(bracketPanel);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GoToSchedule = new javax.swing.JButton();
        GoToLeadboard = new javax.swing.JButton();
        GoToMatch = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        GoToSchedule.setText("To Schedule");
        GoToSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoToScheduleActionPerformed(evt);
            }
        });
        getContentPane().add(GoToSchedule, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 460, 270, 60));

        GoToLeadboard.setText("To Leaderboard");
        GoToLeadboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoToLeadboardActionPerformed(evt);
            }
        });
        getContentPane().add(GoToLeadboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 460, 270, 60));

        GoToMatch.setText("To Match");
        GoToMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoToMatchActionPerformed(evt);
            }
        });
        getContentPane().add(GoToMatch, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 460, 270, 60));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GoToLeadboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToLeadboardActionPerformed
        Leaderboard Board = new Leaderboard();
        this.dispose();
        Board.setVisible(true);
    }//GEN-LAST:event_GoToLeadboardActionPerformed

    private void GoToMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToMatchActionPerformed
        Duel Match = new Duel ();
        this.dispose();
        Match.setVisible(true);
    }//GEN-LAST:event_GoToMatchActionPerformed

    private void GoToScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToScheduleActionPerformed
        Schedule Timings = new Schedule();
        this.dispose();
        Timings.setVisible(true);
    }//GEN-LAST:event_GoToScheduleActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton GoToLeadboard;
    private javax.swing.JButton GoToMatch;
    private javax.swing.JButton GoToSchedule;
    // End of variables declaration//GEN-END:variables
}
