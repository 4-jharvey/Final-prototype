import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;


public class Tournament extends javax.swing.JFrame {
    
    private int tournamentID;
    private JPanel bracketPanel;

    public Tournament(int tournamentID) {
        this.tournamentID = tournamentID;
        initComponents();
        
        System.out.println("TournamentID = " + this.tournamentID);
        
        try{
            BracketGenerator.generateBracket(tournamentID);
            
            getContentPane().setLayout(new BorderLayout());
            
            JPanel buttons = new JPanel(new GridLayout(1,4));
            buttons.add(GoToSchedule);
            buttons.add(GoToLeadboard);
            buttons.add(GoToMatch);
            buttons.add(Registrator);
            
            getContentPane().add(buttons, BorderLayout.NORTH);
            
            bracketPanel = BracketPanel.getBracketPanel(tournamentID);
            
            JScrollPane scrollPane = new JScrollPane(bracketPanel);
            scrollPane.setBorder(null);
            
            getContentPane().add(scrollPane, BorderLayout.CENTER);
            
            System.out.println("Tournament Bracket printed for " + tournamentID);
            
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.getMessage());
        }
    }
        

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GoToSchedule = new javax.swing.JButton();
        GoToLeadboard = new javax.swing.JButton();
        GoToMatch = new javax.swing.JButton();
        Registrator = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        GoToSchedule.setText("To Schedule");
        GoToSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoToScheduleActionPerformed(evt);
            }
        });
        getContentPane().add(GoToSchedule, java.awt.BorderLayout.CENTER);

        GoToLeadboard.setText("To Leaderboard");
        GoToLeadboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoToLeadboardActionPerformed(evt);
            }
        });
        getContentPane().add(GoToLeadboard, java.awt.BorderLayout.PAGE_START);

        GoToMatch.setText("To Match");
        GoToMatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoToMatchActionPerformed(evt);
            }
        });
        getContentPane().add(GoToMatch, java.awt.BorderLayout.PAGE_END);

        Registrator.setText("Register Here");
        Registrator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegistratorActionPerformed(evt);
            }
        });
        getContentPane().add(Registrator, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GoToLeadboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToLeadboardActionPerformed
        // takes user to leaderboard
        Leaderboard Board = new Leaderboard(tournamentID);
        this.dispose();
        Board.setVisible(true);
    }//GEN-LAST:event_GoToLeadboardActionPerformed

    private void GoToMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToMatchActionPerformed
        //takes user to match screen
        Duel Match = new Duel (tournamentID);
        this.dispose();
        Match.setVisible(true);
    }//GEN-LAST:event_GoToMatchActionPerformed

    private void GoToScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToScheduleActionPerformed
        //takes user to schedule
        Schedule Timings = new Schedule(tournamentID);
        this.dispose();
        Timings.setVisible(true);
    }//GEN-LAST:event_GoToScheduleActionPerformed

    private void RegistratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegistratorActionPerformed
        //takes user to registration
        Registration register = new Registration(tournamentID);
        this.dispose();
        register.setVisible(true);
    }//GEN-LAST:event_RegistratorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton GoToLeadboard;
    private javax.swing.JButton GoToMatch;
    private javax.swing.JButton GoToSchedule;
    private javax.swing.JButton Registrator;
    // End of variables declaration//GEN-END:variables
}
