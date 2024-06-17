package cmpt276.group.demo.models.record;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="records")
public class Record {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rid;
    private String username;
    private String patientname;
    private String doctorname;
    private String description;
    private Date date;
    
    public Record() {}
    public Record(String username, String patientname, String doctorname, String description, Date date) {
        this.username = username;
        this.patientname = patientname;
        this.doctorname = doctorname;
        this.description = description;
        this.date = date;
    }
    public Record(String patientname, String doctorname, String description, Date date) {
        this.patientname = patientname;
        this.doctorname = doctorname;
        this.description = description;
        this.date = date;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPatientname() {
        return patientname;
    }
    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }
    public String getDoctorname() {
        return doctorname;
    }
    public void setDoctorname(String doctorname) {
        this.doctorname = doctorname;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    

}
