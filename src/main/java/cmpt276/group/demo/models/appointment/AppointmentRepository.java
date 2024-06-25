package cmpt276.group.demo.models.appointment;


import java.sql.Date;
import java.sql.Time;

import org.springframework.data.jpa.repository.JpaRepository;



public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment findByDoctorName(String doctorName);
    Appointment findByPatientName(String patientName);
    Appointment findByDoctorNameAndDateAndStartTime(String doctorUsername, Date date, Time startTime);
    Appointment findByDoctorUsernameAndDateAndStartTime(String DoctorUsername, Date date, Time starTime);    
}
