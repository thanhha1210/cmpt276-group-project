package cmpt276.group.demo.models.appointment;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment findByDoctorName(String doctorName);
    Appointment findByPatientName(String patientName);
}
