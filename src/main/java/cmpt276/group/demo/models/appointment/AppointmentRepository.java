package cmpt276.group.demo.models.appointment;


import java.sql.Date;
import java.sql.Time;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment findByDoctorUsername(String doctorUsername);
    Appointment findByPatientUsername(String patientUsername);
    Appointment findByPatientUsernameAndDate(String patientUsername, Date date);
    Appointment findByDoctorUsernameAndDateAndStartTime(String doctorUsername, Date date, Time starTime);    
}
