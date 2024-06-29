package cmpt276.group.demo.models.record;

import java.sql.Date;

import cmpt276.group.demo.models.record.Record;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "records")
public class Record implements Comparable<Record>{

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


    @Override
    public int compareTo(Record o) {
        return this.date.compareTo(o.getDate());  
    }

    public Record() {
    }

    public Record(String patientUsername, String patientName, String doctorUsername, String description, Date date) {
        this.patientUsername = patientUsername;
        this.patientName = patientName;
        this.doctorUsername = doctorUsername;
        this.description = description;
        this.date = date;
    }

    public String getPatientUsername() {
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
