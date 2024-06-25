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

import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



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

    //------------------------------------------------------------Get Dashboard-------------------------------------------
    @GetMapping("/admins/getDashboard")
    public String getDashboard(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        model.addAttribute("admin", admin);
        return "admins/mainPage";
    }

    //------------------------------------------------------------View & add doctor-------------------------------------------------------------------------
    @GetMapping("/admins/viewDoctor")
    public String viewDoctor(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        model.addAttribute("admin", admin);
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
        model.addAttribute("success", "Add Successfully");
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
        String doctorName = apt.get("doctorName");
        Date date = Date.valueOf(apt.get("date"));
        Time startTime = Time.valueOf(apt.get("startTime"));
        Appointment deleteApt = appointmentRepo.findByDoctorNameAndDateAndStartTime(doctorName, date, startTime);
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

    // add button in addSchedulePage
    @GetMapping("/admins/addSchedule")
    public String addSchedulePage(Model model) {
        model.addAttribute("schedules", scheduleRepo.findAll());
        return "admins/addSchedulePage";
    }
    
    // return button in addSchedulePage
    @GetMapping("/admins/exitScheduleAdd")
    public String exitAddSchedulePage(Model model) {
        model.addAttribute("schedules", scheduleRepo.findAll());
        return "admins/viewSchedulePage";
    }
    
    @PostMapping("/admins/addSchedule")
    public String postMethodName(@RequestParam Map<String, String> scheduleInfo, HttpServletResponse response, Model model) {
        String doctorUsername = scheduleInfo.get("doctorUsername");

        // Check if any field is empty
        if (doctorUsername == null || doctorUsername.trim().isEmpty() || scheduleInfo.get("duration") == null || scheduleInfo.get("duration").trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                model.addAttribute("error0", "Please enter all the form!");
                return "admins/addSchedulePage";
        }

        // Check if duration is non-negative int
        int duration = Integer.parseInt(scheduleInfo.get("duration"));
        if (duration <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error1", "Please enter a valid duration!");
            return "admins/addSchedulePage";
        }

        // Check if doctor w/ matching doctorUsername exists
        // Add schedule is based on doctorUsername
        Doctor doc = doctorRepo.findByUsername(doctorUsername);
        if (doc == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error2", "Username does not exist. Please enter a valid username!");
            return "admins/addSchedulePage";
        } 

        

        Date date = Date.valueOf(scheduleInfo.get("date"));
        // change to match time format hh:mm:ss
        Time startTime = Time.valueOf(scheduleInfo.get("startTime") + ":00");
        

        // Check if admin adds the same schedule as existing schedule
        if (scheduleRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date, startTime) != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error3", "This schedule already exists. Please enter another schedule!");
            return "admins/addSchedulePage";
        }

        // Get doctorName based on doctorUsername
        Schedule newSchedule = new Schedule(doc.getName(), doctorUsername, date, startTime, duration);
        scheduleRepo.save(newSchedule);
        response.setStatus(201);
        model.addAttribute("Schedule", newSchedule);
        model.addAttribute("success", "Add Schedule Successfully");
        return "admins/addSchedulePage";
    }

    // delete button in viewSchedulePage
    @PostMapping("/admins/deleteSchedule")
    public String deleteSchedule(@RequestParam Map<String, String> scheduleInfo, Model model) {
        String doctorName = scheduleInfo.get("doctorName");
        Date date = Date.valueOf(scheduleInfo.get("date"));
        Time startTime = Time.valueOf(scheduleInfo.get("startTime"));
        Schedule deleteSchedule = scheduleRepo.findByDoctorNameAndDateAndStartTime(doctorName, date, startTime);
        List<Schedule> schedules;
        if (deleteSchedule != null) {
            scheduleRepo.delete(deleteSchedule);
            schedules = scheduleRepo.findAll();
            Collections.sort(schedules);
            model.addAttribute("schedules", schedules);
        }
        return "admins/viewSchedulePage";
    }
}

