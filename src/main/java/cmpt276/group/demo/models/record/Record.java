package cmpt276.group.demo.models.record;

import java.util.Date;

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
    private String username;
    private String patientname;
    private String doctorUserame;
    private String description;
    private Date date;

    public Record() {
    }

    public Record(String username, String patientname, String doctorUserame, String description, Date date) {
        this.username = username;
        this.patientname = patientname;
        this.doctorUserame = doctorUserame;
        this.description = description;
        this.date = date;
    }

    public Record(String patientname, String doctorUserame, String description, Date date) {
        this.patientname = patientname;
        this.doctorUserame = doctorUserame;
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

    public String getdoctorUserame() {
        return doctorUserame;
    }

    public void setdoctorUserame(String doctorUserame) {
        this.doctorUserame = doctorUserame;
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
