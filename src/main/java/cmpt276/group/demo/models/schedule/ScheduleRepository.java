package cmpt276.group.demo.models.schedule;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Schedule findByDoctorName(String doctorName);
    List<Schedule> findByDoctorUsernameAndDate(String doctorUsername, Date date);
    Schedule findByDoctorUsernameAndDateAndStartTime(String doctorUsername, Date date, Time starTime);
    Schedule findByDoctorNameAndDateAndStartTime(String doctorName, Date date, Time starTime);
}

