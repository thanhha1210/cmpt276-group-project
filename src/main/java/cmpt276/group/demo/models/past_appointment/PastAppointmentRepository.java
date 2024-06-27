package cmpt276.group.demo.models.past_appointment;


import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PastAppointmentRepository extends JpaRepository<PastAppointment, Integer> {
    List<PastAppointment> findByDoctorUsername(String doctorUsername);
    List<PastAppointment> findByPatientUsername(String patientUsername);
    PastAppointment findByPatientUsernameAndDoctorUsernameAndDate(String patientUsername, String doctorUsername, Date date);
    PastAppointment findByDoctorUsernameAndDateAndStartTime(String doctorUsername, Date date, Time starTime);    
}
