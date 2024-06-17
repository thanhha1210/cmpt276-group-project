package cmpt276.group.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.record.RecordRepository;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class AdminController {
    
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private RecordRepository recordRepo;
    // @Autowired
    // private ScheduleRepository scheduleRepo;
    // @Autowired
    // private AppointmentRepository appointmentRepo;

    // View & add doctor
    @GetMapping("/admins/viewDoctor")
    public String viewDoctor(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }
    
    @GetMapping("/admins/addDoctor")
    public String addDoctorPage(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/addDoctorPage";
    }
    
    @PostMapping("/admins/addDoctor")
    public String registerDoctor(@RequestParam Map<String, String> formData, HttpServletResponse response, Model model) {
        if (formData.get("age").equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please enter all the form!");
            return "admins/addDoctorPage";
        }
        String username = formData.get("username");
        String password = formData.get("password");
        String name = formData.get("name");
        int age = Integer.parseInt(formData.get("age"));
        String address = formData.get("address");
        String phone = formData.get("phone");

        if (username.trim().equals("") || password.trim().equals("") || name.trim().equals("") || address.trim().equals("") || phone.trim().equals("") ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please enter all the form!");
            return "admins/addDoctorPage";
        }
        if (age <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error1", "Please enter a valid age");
            return "admins/addDoctorPage";
        }
        if (doctorRepo.findByUsername(username) != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error2", "This username already exists. Please enter another username!");
            return "admins/addDoctorPage";
        }

        Doctor newDoctor = new Doctor(username, password, name, age, address, phone);
        doctorRepo.save(newDoctor);
        response.setStatus(201);
        model.addAttribute("Doctor", newDoctor);
        return "admins/addDoctorPage";
    }

    // Deletes a rectangle by name.
    @PostMapping("/admins/deleteDoctor")
    public String deleteRectangle(@RequestParam String username, Model model) {
        Doctor temp = doctorRepo.findByUsername(username);
        if (temp != null) {
            doctorRepo.delete(temp);
        }
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }

    // Return display page
    @GetMapping("/admins/exitDoctorAdd")
    public String exitAddDoctorPage(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }
   
    @GetMapping("/admins/getDashboard")
    public String getDashboard() {
        return "admins/mainPage";
    }
    

    // View & add schedule
    // @GetMapping("/admins/viewSchedule")
    // public String getViewSchedulePage(Model model) {
    //      // Replace with actual logic to retrieve schedule information
    //     Timestamp startTime = new Timestamp(System.currentTimeMillis());  // Replace with actual start time logic
    //     Timestamp endTime = new Timestamp(startTime.getTime() + 3600000);  // Adding 1 hour to start time
        
    //     Schedule sche = new Schedule("Dr. Smith", startTime, endTime);
        
    //     model.addAttribute("sche", sche);
    //     return "admins/viewSchedulePage";
    // }

  
   
   
    
    

    // View & delete appointment
}
