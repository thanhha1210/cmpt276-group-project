package cmpt276.group.demo.models.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.sql.Date;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByDoctorUsername(String doctorUsername);     // 1 doctor can have many feedback
    List<Feedback> findByPatientUsername(String patientUsername);   // 1 patient can have many feedback
    Feedback findByDoctorUsernameAndPatientUsernameAndDate(String doctorUsername, String patientUsername, Date date);
}