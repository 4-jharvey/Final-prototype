import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.time.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ScheduleGenerator extends JPanel{
    
    public static class matchInfo{
        String teamA;
        String teamB;
        String winner;
        LocalTime time;
        
        public matchInfo(String teamB, String teamA, String winner, LocalTime time){
            this.teamA = teamA;
            this.teamB = teamB;
            this.winner = winner;
            this.time = time;
            
        }
            
    }
    
    public static List<matchInfo> getMatchInfo(int tournamentID){
        List<matchInfo> schedule = new ArrayList<>();
        LocalTime currentTime = LocalTime.now();
        
        try(Connection connect = DatabaseConnection.getConnection()){
            String query = "SELECT Duel.MatchID, Duel.TeamA, Duel.TeamB, Duel.Winner, Duel.Round, "
                           + "TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, WinnerTeam.TeamName AS WinnerName " 
                           + "FROM Duel " 
                           + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID " 
                           + "LEFT JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID "
                           + "LEFT JOIN Team AS WinnerTeam ON Duel.Winner = WinnerTeam.TeamID " 
                           + "WHERE Duel.TournamentID = ? AND Duel.Round <> '100' "
                           + "ORDER BY  Duel.MatchID ASC";
            PreparedStatement psTeam = connect.prepareStatement(query);
            psTeam.setInt(1, tournamentID);
            ResultSet rsTeams = psTeam.executeQuery();
            
            int count = 0;
            
            while(rsTeams.next()){
                String teamA = rsTeams.getString("TeamAName");
                String teamB = rsTeams.getString("TeamBName");
                String winner = rsTeams.getString("WinnerName");
                LocalTime matchTime = currentTime.plusMinutes(30 * count);
                
                schedule.add(new ScheduleGenerator.matchInfo(teamA, teamB, winner, matchTime));
                count++;
            }
            
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQL Error: " + ex.toString());
        }  catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unexpected error " + ex.toString());
        }
        return schedule;
    }
    
    public ScheduleGenerator(List<matchInfo> schedule){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(255,255,255));
        
        for(matchInfo match : schedule){
            add(createScheduleBox(match));
            add(Box.createVerticalStrut(10));
        }
    }
    
    private JPanel createScheduleBox(matchInfo match){
        JPanel box = new JPanel (new GridLayout(3, 1));
        box.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        box.setBackground(new Color(255, 255, 255));
        
        DateTimeFormatter Format = DateTimeFormatter.ofPattern("HH:mm");
                
        JLabel Time = new JLabel("Time: " + match.time.format(Format));
        JLabel Vs = new JLabel(match.teamA + " vs " + match.teamB);
        JLabel Winner = new JLabel("Winner: " + match.winner);
        
        box.add(Time);
        box.add(Vs);
        box.add(Winner);
        
        return box;
    }
    
    public static JScrollPane createSchedule(List<matchInfo> schedule){
        ScheduleGenerator schedulePane = new ScheduleGenerator(schedule);
        return new JScrollPane(schedulePane);
    }
    
    public static JScrollPane refresher(int tournamentID){
        List<matchInfo> schedule = getMatchInfo(tournamentID);
        return createSchedule(schedule);
    }
        
}
