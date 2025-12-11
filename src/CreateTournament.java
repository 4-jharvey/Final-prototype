
import javax.swing.*;
import java.sql.*;
import javax.swing.DefaultComboBoxModel;

public class CreateTournament extends javax.swing.JFrame {


 
    public CreateTournament() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
       //ALl available games to play
        String[] gameName = {
            "Valorant",
            "Rainbow Six Siege",
            "Rocket League",
            "CSGO"
        };
        
        GamePlayed.setModel(new DefaultComboBoxModel<>(gameName));
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        TournamentName = new javax.swing.JTextField();
        NumOfTeams = new javax.swing.JTextField();
        GamePlayed = new javax.swing.JComboBox<>();
        PrizePoolDistribution = new javax.swing.JFormattedTextField();
        DateOfTournament = new javax.swing.JFormattedTextField();
        PrizePool = new javax.swing.JFormattedTextField();
        CreateTournament = new javax.swing.JButton();
        BackToMenu = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Prize Pool Distribution (Optional)");
        jLabel1.setToolTipText("");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 300, 50));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Tournament Name");
        jLabel2.setToolTipText("");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 50));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Game");
        jLabel3.setToolTipText("");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 210, 50));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("Number of teams/players");
        jLabel4.setToolTipText("");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 250, 50));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Date of tournament");
        jLabel5.setToolTipText("");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 210, 50));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Prize Pool (Optional)");
        jLabel6.setToolTipText("");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 210, 50));

        TournamentName.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        TournamentName.setToolTipText("");
        TournamentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentNameActionPerformed(evt);
            }
        });
        getContentPane().add(TournamentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 340, 50));

        NumOfTeams.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        NumOfTeams.setToolTipText("");
        NumOfTeams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NumOfTeamsActionPerformed(evt);
            }
        });
        getContentPane().add(NumOfTeams, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, 360, 50));

        GamePlayed.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        GamePlayed.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        GamePlayed.setToolTipText("");
        GamePlayed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GamePlayedActionPerformed(evt);
            }
        });
        getContentPane().add(GamePlayed, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 340, 30));

        PrizePoolDistribution.setText("1st: £     2nd: £        3rd: £");
        PrizePoolDistribution.setToolTipText("");
        PrizePoolDistribution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrizePoolDistributionActionPerformed(evt);
            }
        });
        getContentPane().add(PrizePoolDistribution, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 280, 360, 50));

        DateOfTournament.setText("/  /");
        DateOfTournament.setToolTipText("");
        DateOfTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DateOfTournamentActionPerformed(evt);
            }
        });
        getContentPane().add(DateOfTournament, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, 360, 50));

        PrizePool.setText("£");
        PrizePool.setToolTipText("");
        PrizePool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrizePoolActionPerformed(evt);
            }
        });
        getContentPane().add(PrizePool, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 220, 360, 50));

        CreateTournament.setText("Create tournament");
        CreateTournament.setToolTipText("");
        CreateTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateTournamentActionPerformed(evt);
            }
        });
        getContentPane().add(CreateTournament, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 340, 330, 50));

        BackToMenu.setText("Back to menu");
        BackToMenu.setToolTipText("");
        BackToMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackToMenuActionPerformed(evt);
            }
        });
        getContentPane().add(BackToMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 290, 50));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 410));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void TournamentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TournamentNameActionPerformed

    private void NumOfTeamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NumOfTeamsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumOfTeamsActionPerformed

    private void PrizePoolDistributionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrizePoolDistributionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PrizePoolDistributionActionPerformed

    private void DateOfTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DateOfTournamentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DateOfTournamentActionPerformed

    private void PrizePoolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrizePoolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PrizePoolActionPerformed

    private void BackToMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackToMenuActionPerformed
        //sends users back to the homepage
        HomePage Home = new HomePage();
        this.dispose();
        Home.setVisible(true);
    }//GEN-LAST:event_BackToMenuActionPerformed

    private void GamePlayedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GamePlayedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_GamePlayedActionPerformed

    private void CreateTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateTournamentActionPerformed
        //gets required data from the form
        String TournyName = TournamentName.getText();
        String Date = DateOfTournament.getText();
        String NumberOfPlayers = NumOfTeams.getText();
        String Game = (String) GamePlayed.getSelectedItem();
        
        //makes sure all required data is present before moving to tournament
        if(TournyName.equals("") || Date.equals("") || NumberOfPlayers.equals("") || Game == null){
            
            JOptionPane.showMessageDialog(null,"Please complete the settings for this tournament.");
            return;
        }
        
        //inputs all data gathered about tournament into the database
        try(Connection connect = DatabaseConnection.getConnection()){
            String sql = "INSERT INTO Tournament(Name, Date, NumOfTeams, GameTitle) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, TournyName);
            ps.setString(2, Date);
            ps.setString(3, NumberOfPlayers);
            ps.setString(4, Game);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            
            
            // This shows the data has been entered corectly and directs the user to the next screen
            if (rs.next()){
                JOptionPane.showMessageDialog(null,"Tournament created Successfully");
                
                int tournamentID = rs.getInt(1);
                
                Tournament tournament = new Tournament(tournamentID);
                this.dispose();
                tournament.setVisible(true);
            }
                        
            
            // catches any errors that occur
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL error " + ex.getMessage());
        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
            
            
    }//GEN-LAST:event_CreateTournamentActionPerformed

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
            java.util.logging.Logger.getLogger(CreateTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateTournament().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackToMenu;
    private javax.swing.JButton CreateTournament;
    private javax.swing.JFormattedTextField DateOfTournament;
    private javax.swing.JComboBox<String> GamePlayed;
    private javax.swing.JTextField NumOfTeams;
    private javax.swing.JFormattedTextField PrizePool;
    private javax.swing.JFormattedTextField PrizePoolDistribution;
    private javax.swing.JTextField TournamentName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
