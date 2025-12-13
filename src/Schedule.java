import java.awt.*;
import javax.swing.*;
import java.util.List;

public class Schedule extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Schedule.class.getName());

    private int tournamentID;
    private JScrollPane SchedulePane;
    
    public Schedule(int tournamentID) {
        this.tournamentID = tournamentID;
        initComponents();
        //JFrame fills the screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        //calls upon the method to show the schedule
        showSchedule();
        
        //adds button to the JPanel format
        JPanel button = new JPanel(new GridLayout(1, 1));
        button.add(BackToBracket);
        
        // adds button JPanel tp the JFrame
        getContentPane().add(button, BorderLayout.SOUTH);
    }
    
    private void showSchedule(){
        //Gets the List of matches and uses schedule generator to turn it into a schedule
        List<ScheduleGenerator.matchInfo> matches = ScheduleGenerator.getMatchInfo(tournamentID);
        SchedulePane = ScheduleGenerator.createSchedule(matches);
        
        //Adds Schedule to JFrame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(SchedulePane, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BackToBracket = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BackToBracket.setText("Back to Bracket");
        BackToBracket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackToBracketActionPerformed(evt);
            }
        });
        getContentPane().add(BackToBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 550, 190, 40));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BackToBracketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackToBracketActionPerformed
        //takes user back to tournament screen
        Tournament Tourny = new Tournament(tournamentID);
        this.dispose();
        Tourny.setVisible(true);
    }//GEN-LAST:event_BackToBracketActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackToBracket;
    // End of variables declaration//GEN-END:variables
}
