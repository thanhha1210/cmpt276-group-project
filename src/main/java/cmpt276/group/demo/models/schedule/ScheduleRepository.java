package cmpt276.group.demo.models.schedule;
import java.sql.Date;
import java.sql.Time;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Schedule findByDoctorName(String doctorName);
    Schedule findByDoctorUsernameAndDateAndStartTime(String doctorUsername, Date date, Time starTime);
    Schedule findByDoctorNameAndDateAndStartTime(String doctorName, Date date, Time starTime);
}
