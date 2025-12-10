import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.sql.*;
import javax.swing.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 4-JHarvey
 */
public class LoadTournament extends javax.swing.JFrame {

    private JPanel TournyList;
    private Integer selectedID = null;
    
    public LoadTournament() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setLayout(new BorderLayout());
        
        JPanel button = new JPanel(new GridLayout(1, 2));
        button.add(LoadData);
        button.add(BackToMenu);
        getContentPane().add(button, BorderLayout.SOUTH);
        
        loadTournaments();
    }

    private void loadTournaments(){
        TournyList = new JPanel();
        TournyList.setLayout(new BoxLayout(TournyList, BoxLayout.Y_AXIS));
        
        try(Connection connect = DatabaseConnection.getConnection()){
            String sqlTourn = "SELECT TournamentID, Date, Name, Tournament_winner "
                              + "FROM Tournament";
            PreparedStatement psTourns = connect.prepareStatement(sqlTourn);
            ResultSet rsTourns = psTourns.executeQuery();
            
            while(rsTourns.next()){
                String Date = rsTourns.getString("Date");
                String Tournament_Name = rsTourns.getString("Name");
                String Winner = rsTourns.getString("Tournament_winner");
                int TournyID = rsTourns.getInt("TournamentID");
                
                JPanel box = new JPanel();
                box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
                box.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
                box.setBackground(new Color(255, 255, 255));
                
                JLabel date = new JLabel("Date: " + Date);
                JLabel name = new JLabel(Tournament_Name);
                JLabel winner = new JLabel("Winner: " + Winner);
                
                box.add(date);
                box.add(name);
                box.add(winner);
                
                box.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent e){
                        selectedID = TournyID;                       
                    }
                
                
                });
                
                TournyList.add(box);
                TournyList.add(Box.createVerticalStrut(10));
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL Error: " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
        
        JScrollPane tournyList = new JScrollPane (TournyList);
        getContentPane().add(tournyList, BorderLayout.CENTER);
        revalidate();
        repaint();
                
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LoadData = new javax.swing.JButton();
        BackToMenu = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        LoadData.setText("Load Data");
        LoadData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadDataActionPerformed(evt);
            }
        });
        getContentPane().add(LoadData, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 360, 120, 50));

        BackToMenu.setText("Back to menu");
        BackToMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackToMenuActionPerformed(evt);
            }
        });
        getContentPane().add(BackToMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 300, 120, 50));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LoadDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadDataActionPerformed
        if(selectedID != null){
            Tournament Tourny = new Tournament(selectedID);
            this.dispose();
            Tourny.setVisible(true);            
        }
    }//GEN-LAST:event_LoadDataActionPerformed

    private void BackToMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackToMenuActionPerformed
        //takes users back to the homepage
        HomePage Home = new HomePage();
        this.dispose();
        Home.setVisible(true);
    }//GEN-LAST:event_BackToMenuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoadTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoadTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoadTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoadTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoadTournament().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackToMenu;
    private javax.swing.JButton LoadData;
    // End of variables declaration//GEN-END:variables
}
