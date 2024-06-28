package cmpt276.group.demo.models.record;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.record.Record;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    List<Record> findByPatientUsername(String patientUsername);

    List<Record> findByDoctorUsername(String doctorUserame);

    Record findByPatientUsernameAndDoctorUsernameAndDate(String patientUsername, String doctorUsername, Date date);

}