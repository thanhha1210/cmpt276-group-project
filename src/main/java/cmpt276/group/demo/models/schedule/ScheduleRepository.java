package cmpt276.group.demo.models.schedule;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByDoctorUsername(String doctorUsername);
    List<Schedule> findByDoctorUsernameAndDate(String doctorUsername, Date date);
    Schedule findByDoctorUsernameAndDateAndStartTime(String doctorUsername, Date date, Time starTime);
}

