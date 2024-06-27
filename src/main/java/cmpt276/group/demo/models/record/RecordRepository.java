package cmpt276.group.demo.models.record;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RecordRepository extends JpaRepository<Record, Integer> {
    List<Record> findByUsername(String username);
    List<Record> findByDoctorUserame(String doctorUserame);
}