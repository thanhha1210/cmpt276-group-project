package cmpt276.group.demo.controllers;

import java.sql.Date;
import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.ScheduleRepository;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class AdminController {
    
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private RecordRepository recordRepo;
    @Autowired
    private ScheduleRepository scheduleRepo;
    @Autowired
    private AppointmentRepository appointmentRepo;



    //------------------------------------------------------------Dash Board-------------------------------------------------------------------------
    @GetMapping("/admins/getDashboard")
    public String getDashboard() {
        return "admins/mainPage";
    }



    //------------------------------------------------------------View & add doctor-------------------------------------------------------------------------
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



    //------------------------------------------------------ Deletes a doctor------------------------------------------------------
    @PostMapping("/admins/deleteDoctor")
    public String deleteDoctor(@RequestParam String username, Model model) {
        Doctor temp = doctorRepo.findByUsername(username);
        if (temp != null) {
            doctorRepo.delete(temp);
        }
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }

    // Return doctor display page
    @GetMapping("/admins/exitDoctorAdd")
    public String exitAddDoctorPage(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }
    
    

    //------------------------------------------------------ View & delete appointment----------------------------------------------------
    // view appointment
    @GetMapping("/admins/viewAppointment")
    public String viewAppointment(Model model) {
        List<Appointment> appointments = appointmentRepo.findAll();
        Collections.sort(appointments);
        model.addAttribute("appointments", appointments);
        return "admins/viewAppointmentPage";
    }
    
   @PostMapping("/admins/deleteAppointment")
    public String deleteAppointment(@RequestParam Map<String, String> apt, Model model, HttpServletResponse response) {
        String doctorName = apt.get("doctorName");      // adjust
        Date date = Date.valueOf(apt.get("date"));
        Time startTime = Time.valueOf(apt.get("startTime"));
        Appointment deleteApt = appointmentRepo.findByDoctorNameAndDateAndStartTime(doctorName, date, startTime);   // adjust
        List<Appointment> appointments;
        if (deleteApt != null) {
            appointmentRepo.delete(deleteApt);
            appointments = appointmentRepo.findAll();
            Collections.sort(appointments);
            model.addAttribute("appointments", appointments);
        } 

        return "admins/viewAppointmentPage";
   }



    //------------------------------------------------------ View, add & delete schedule----------------------------------------------------
    @GetMapping("/admins/viewSchedule")
    public String viewSchedule(Model model) {
        model.addAttribute("schedules", scheduleRepo.findAll());
        return "admins/viewSchedulePage";
    }
   

}
