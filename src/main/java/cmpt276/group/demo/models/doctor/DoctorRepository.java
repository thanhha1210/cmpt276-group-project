package cmpt276.group.demo.models.doctor;

import org.springframework.data.jpa.repository.JpaRepository;


public interface  DoctorRepository extends JpaRepository<Doctor,Integer> { // interface with the table (can find, update, delete certain item based on Doctor class) 
    Doctor findByUsername(String username);
    Doctor findByUsernameAndPassword(String username, String password);
}
