import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.time.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ScheduleGenerator extends JPanel{
    //what is the class requires to work
    public static class matchInfo{
        String teamA;
        String teamB;
        String winner;
        LocalDateTime time;
        
        public matchInfo(String teamB, String teamA, String winner, LocalDateTime time){
            this.teamA = teamA;
            this.teamB = teamB;
            this.winner = winner;
            this.time = time;
            
        }
            
    }
    
    //this will get necessary match info
    public static List<matchInfo> getMatchInfo(int tournamentID){
        List<matchInfo> schedule = new ArrayList<>();
        LocalTime currentTime = LocalTime.now();
        
        try(Connection connect = DatabaseConnection.getConnection()){
            //sql query to the database
            String query = "SELECT Duel.MatchID, Duel.TeamA, Duel.TeamB, Duel.Winner, Duel.Round, "
                           + "TeamA.TeamName AS TeamAName, TeamB.TeamName AS TeamBName, WinnerTeam.TeamName AS WinnerName, Schedule.Time " 
                           + "FROM Duel " 
                           + "JOIN Team AS TeamA ON Duel.TeamA = TeamA.TeamID " 
                           + "LEFT JOIN Team AS TeamB ON Duel.TeamB = TeamB.TeamID "
                           + "LEFT JOIN Team AS WinnerTeam ON Duel.Winner = WinnerTeam.TeamID "
                           + "JOIN Schedule ON Duel.MatchID = Schedule.MatchID " 
                           + "WHERE Duel.TournamentID = ? AND Duel.Round <> '100' "
                           + "ORDER BY  Duel.MatchID ASC";
            PreparedStatement psTeam = connect.prepareStatement(query);
            psTeam.setInt(1, tournamentID);
            ResultSet rsTeams = psTeam.executeQuery();
            
            int count = 0;
            
            while(rsTeams.next()){
                //turns obtained data into variables
                String teamA = rsTeams.getString("TeamAName");
                String teamB = rsTeams.getString("TeamBName");
                String winner = rsTeams.getString("WinnerName");
                Timestamp time = rsTeams.getTimestamp("Time");
                
                //gets the local time from the system if time is empty
                LocalDateTime matchTime = time != null ? time.toLocalDateTime(): null;
                
                //adds the data to the list
                schedule.add(new ScheduleGenerator.matchInfo(teamA, teamB, winner, matchTime));
                // counts every match
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
    
    //creates the box for each match to be held in
    public ScheduleGenerator(List<matchInfo> schedule){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(255,255,255));
        
        //for every match in schedule a box is creates
        for(matchInfo match : schedule){
            add(createScheduleBox(match));
            add(Box.createVerticalStrut(10));
        }
    }
    
    // what is put inside the boxes
    private JPanel createScheduleBox(matchInfo match){
        JPanel box = new JPanel (new GridLayout(3, 1));
        box.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        box.setBackground(new Color(255, 255, 255));
        
        //formats the time
        DateTimeFormatter Format = DateTimeFormatter.ofPattern("HH:mm");
                
        //Creates labels for each input in the box
        JLabel Time = new JLabel("Time: " + match.time.format(Format));
        JLabel Vs = new JLabel(match.teamA + " vs " + match.teamB);
        JLabel Winner = new JLabel("Winner: " + match.winner);
        
        //adds the labels to the box
        box.add(Time);
        box.add(Vs);
        box.add(Winner);
        
        return box;
    }
    
    //turn the scheudle into a scroll pane
    public static JScrollPane createSchedule(List<matchInfo> schedule){
        ScheduleGenerator schedulePane = new ScheduleGenerator(schedule);
        return new JScrollPane(schedulePane);
    }
    
    //refreshes the schedule to make sure the data is up to date
    public static JScrollPane refresher(int tournamentID){
        List<matchInfo> schedule = getMatchInfo(tournamentID);
        return createSchedule(schedule);
    }
        
}
