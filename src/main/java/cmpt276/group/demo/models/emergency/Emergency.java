package cmpt276.group.demo.models.emergency;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="emergencies")
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eid;

    @Column(name = "emer_id")
    private String emerId;

    private String username;
    
    @Column(name = "doctor_username")
    private String doctorUsername; 
    
    @Column(name = "room_name")
    private String roomName;

    private Date date;
    private Time startTime;
    private int severity;
    private int duration;
    private boolean isFinish;

    public Emergency() {}
    
    public Emergency(String emerId, String username, String doctorUsername, String roomName, int severity) {
        this.emerId = emerId;
        this.username = username;
        this.doctorUsername = doctorUsername;
        this.roomName = roomName;
        
        LocalDate dateNow = LocalDateTime.now().minusHours(7).toLocalDate();
        LocalTime timeNow = LocalDateTime.now().minusHours(7).toLocalTime();

        this.date =  Date.valueOf(dateNow);
        this.startTime = Time.valueOf(timeNow);
        this.duration = 0;
        this.isFinish = false;
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getDoctorUsername() {
        return doctorUsername;
    }
    public void setDoctorUsername(String doctorUsername) {
        this.doctorUsername = doctorUsername;
    }
    public String getRoomName() {
        return roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Time getStartTime() {
        return startTime;
    }
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }
    public int getSeverity() {
        return severity;
    }
    public void setSeverity(int severity) {
        this.severity = severity;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public boolean isFinish() {
        return isFinish;
    }
    public void setFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }
    public String getEmerId() {
        return emerId;
    }

}
