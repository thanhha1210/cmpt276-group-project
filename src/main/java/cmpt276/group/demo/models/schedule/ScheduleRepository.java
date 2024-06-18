package cmpt276.group.demo.models.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Schedule findByDoctorName(String doctorName);
}
