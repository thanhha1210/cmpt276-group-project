package cmpt276.group.demo.models.patient;

import org.springframework.data.jpa.repository.JpaRepository;


public interface  PatientRepository extends JpaRepository<Patient,Integer> { // interface with the table (can find, update, delete certain item based on Patient class) 
    Patient findByUsername(String username);
    Patient findByUsernameAndPassword(String username, String password);
}
