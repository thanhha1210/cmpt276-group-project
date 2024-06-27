package cmpt276.group.demo.models.record;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "records")
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rid;

    @Column(name = "patient_username")
    private String patientUsername;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "doctor_username")
    private String doctorUsername;
    
    private String description;
    private Date date;

    public Record() {
    }

    public Record(String patientUsername, String patientName, String doctorUsername, String description, Date date) {
        this.patientUsername = patientUsername;
        this.patientName = patientName;
        this.doctorUsername = doctorUsername;
        this.description = description;
        this.date = date;
    }

    public String getpatientUsername() {
        return patientUsername;
    }
    public String getPatientName() {
        return patientName;
    }
    public String getDoctorUsername() {
        return doctorUsername;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String desc) {
        this.description = desc;
    }
    public Date getDate() {
        return date;
    }

}
