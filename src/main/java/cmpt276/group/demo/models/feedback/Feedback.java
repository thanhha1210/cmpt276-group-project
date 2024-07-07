package cmpt276.group.demo.models.feedback;


import java.sql.Date;

import cmpt276.group.demo.models.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedbacks")
public class Feedback implements Comparable<Feedback>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fid;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "doctor_username")
    private String doctorUsername;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_username")
    private String patientUsername;

    private Date date;

    private Department department;

    private String description;

    public Feedback() { }

    public Feedback(String doctorName, String doctorUsername, String patientName, String patientUsername, Date date, Department department, String description) {
        this.doctorName = doctorName;
        this.doctorUsername = doctorUsername;
        this.patientName = patientName;
        this.patientUsername = patientUsername;
        this.date = date;
        this.department = department;
        this.description = description;
    }

    @Override
    public int compareTo(Feedback o) {
        return o.getDate().compareTo(date);
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setFeedback(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
