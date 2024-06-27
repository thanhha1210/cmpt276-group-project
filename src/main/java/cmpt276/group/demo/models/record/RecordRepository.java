package cmpt276.group.demo.models.record;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cmpt276.group.demo.models.record.Record;


public interface RecordRepository extends JpaRepository<Record, Integer> {
    List<Record> findByPatientUsername(String patientUsername);
    List<Record> findByDoctorUsername(String doctorUserame);
}