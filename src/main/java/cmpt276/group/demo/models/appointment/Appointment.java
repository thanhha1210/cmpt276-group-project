package cmpt276.group.demo.models.appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")

public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;
    private String doctorName;
    private String doctorUsername;
    private String patientName;
    private String date;
    private String startTime;
    private int duration;

    public Appointment() { }
    
    public Appointment(String doctorName, String doctorUsername, String patientName, String date, String startTime, int duration) {
        this.doctorName = doctorName;
        this.doctorUsername = doctorUsername;
        this.patientName = patientName;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getDoctorName() {
        return doctorName;
    }
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    public String getDoctorUsername() {
        return doctorUsername;
    }
    public void setDoctorUsername(String doctorUsername) {
        this.doctorUsername = doctorUsername;
    }
    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }   
}
