import java.awt.*;
import javax.swing.*;

public class Schedule extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Schedule.class.getName());
    private JPanel schedule;

    private int tournamentID;
    
    public Schedule(int tournamentID) {
        this.tournamentID = tournamentID;
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        getContentPane().setLayout(new BorderLayout());
        
        JPanel button =;
        
        JScrollPane scheduel = new JScrollPane(schedule);
        scheduel.setBorder(null);
        
        scheduel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scheduel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
