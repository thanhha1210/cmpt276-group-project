package cmpt276.group.demo.models.patient;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;        
    private String username;
    private String password;
    private String name;
    private int age;
    private String address;
    private String phone;

    
    public Patient() { }

    public Patient(String username, String password, String name, int age, String address, String phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.address = address;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getPhonenumber() {
        return phone;
    }
    public void setPhonenumber(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
}