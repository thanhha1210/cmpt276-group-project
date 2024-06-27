package cmpt276.group.demo.models.past_appointment;


import java.sql.Date;
import java.sql.Time;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PastAppointmentRepository extends JpaRepository<PastAppointment, Integer> {
    PastAppointment findByDoctorUsername(String doctorUsername);
    PastAppointment findByPatientUsername(String patientUsername);
    PastAppointment findByPatientUsernameAndDate(String patientUsername, Date date);
    PastAppointment findByDoctorUsernameAndDateAndStartTime(String doctorUsername, Date date, Time starTime);    
}
