import java.awt.*;
import javax.swing.*;


public class Tournament extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Tournament.class.getName());
    
    
    private int tournamentID;
    private BracketPanel bracketPanel;

    public Tournament(int tournamentID) {
        this.tournamentID = tournamentID;
        initComponents();
        
        //generates the image of the bracket created in a specific area
        // also runs the Bracket Generation and Bracket Panel code
        try{
            BracketGenerator.generateBracket(tournamentID);
        
            bracketPanel = new BracketPanel(tournamentID);
            getContentPane().add(
                    bracketPanel,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 850, 400)
            );
            
            System.out.print("Tournament created for " + tournamentID);
            // catches any errors
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error ocurred " + e.getMessage());
        }
    }
        

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GoToSchedule = new javax.swing.JButton();
        GoToLeadboard = new javax.swing.JButton();
        GoToMatch = new javax.swing.JButton();
        Registrator = new javax.swing.JButton();
        GenerateBracket = new javax.swing.JButton();

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

        Registrator.setText("Register Here");
        Registrator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegistratorActionPerformed(evt);
            }
        });
        getContentPane().add(Registrator, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 530, 210, 40));

        GenerateBracket.setText("Generate Bracket");
        GenerateBracket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenerateBracketActionPerformed(evt);
            }
        });
        getContentPane().add(GenerateBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 530, 210, 40));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GoToLeadboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToLeadboardActionPerformed
        // takes user to leaderboard
        Leaderboard Board = new Leaderboard();
        this.dispose();
        Board.setVisible(true);
    }//GEN-LAST:event_GoToLeadboardActionPerformed

    private void GoToMatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToMatchActionPerformed
        //takes user to match screen
        Duel Match = new Duel ();
        this.dispose();
        Match.setVisible(true);
    }//GEN-LAST:event_GoToMatchActionPerformed

    private void GoToScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoToScheduleActionPerformed
        //takes user to schedule
        Schedule Timings = new Schedule();
        this.dispose();
        Timings.setVisible(true);
    }//GEN-LAST:event_GoToScheduleActionPerformed

    private void RegistratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegistratorActionPerformed
        //takes user to registration
        Registration register = new Registration(this.tournamentID);
        this.dispose();
        register.setVisible(true);
    }//GEN-LAST:event_RegistratorActionPerformed

    private void GenerateBracketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateBracketActionPerformed
        //updates the bracket
        BracketGenerator.generateBracket(tournamentID);
        bracketPanel.repaint();
    }//GEN-LAST:event_GenerateBracketActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton GenerateBracket;
    private javax.swing.JButton GoToLeadboard;
    private javax.swing.JButton GoToMatch;
    private javax.swing.JButton GoToSchedule;
    private javax.swing.JButton Registrator;
    // End of variables declaration//GEN-END:variables
}
