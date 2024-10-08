package cmpt276.group.demo.models.appointment;


import java.sql.Time;

import cmpt276.group.demo.models.Department;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment implements Comparable<Appointment>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "doctor_username")
    private String doctorUsername;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_username")
    private String patientUsername;

    private Date date;

    @Column(name = "start_time")
    private Time startTime;
    
    private int duration;

    private Department department;

    public Appointment() { }
    

    public Appointment(String doctorName, String doctorUsername, String patientName, String patientUsername, Date date, Time startTime, int duration, Department department) {
        this.doctorName = doctorName;
        this.doctorUsername = doctorUsername;
        this.patientName = patientName;
        this.patientUsername = patientUsername;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.department = department;
    }

    @Override
    public int compareTo(Appointment o) {
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


    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    public String getPatientUsername() {
        return patientUsername;
    }
    public void setPatientUsername(String patientUsername) {
        this.patientUsername = patientUsername;
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
