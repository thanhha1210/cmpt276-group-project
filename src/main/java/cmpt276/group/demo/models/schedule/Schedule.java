package cmpt276.group.demo.models.schedule;

import java.sql.Date;
import java.sql.Time;

import cmpt276.group.demo.models.Department;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedules")
public class Schedule implements Comparable<Schedule>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "doctor_username")
    private String doctorUsername;

    private Date date;

    @Column(name = "start_time")
    private Time startTime;
    
    private int duration;

    private Department department;

    public Schedule() { }

    
    public Schedule(String doctorName, String doctorUsername, Date date, Time startTime, int duration, Department department) {
        this.doctorName = doctorName;
        this.doctorUsername = doctorUsername;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.department = department;
    }

    @Override
    public int compareTo(Schedule o) {
        if (o.getDate() != this.date) {
            return this.date.compareTo(o.getDate());
        }
        else {
            return this.startTime.compareTo(o.getStartTime());
        }
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
