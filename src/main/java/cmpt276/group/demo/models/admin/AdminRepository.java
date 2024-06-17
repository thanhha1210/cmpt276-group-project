package cmpt276.group.demo.models.admin;

import org.springframework.data.jpa.repository.JpaRepository;


public interface  AdminRepository extends JpaRepository<Admin,Integer> { // interface with the table (can find, update, delete certain item based on Nurse class) 
    Admin findByUsername(String username);
    Admin findByUsernameAndPassword(String username, String password);
}