package cmpt276.group.demo.models.emergency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyRepository extends JpaRepository <Emergency, Integer>{
    Emergency findByEmerId(String emerId);
    
}
