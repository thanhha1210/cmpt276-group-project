package cmpt276.group.demo.controllers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.group.demo.models.Department;
import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.emergency.Emergency;
import cmpt276.group.demo.models.emergency.EmergencyRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.room.Room;
import cmpt276.group.demo.models.room.RoomRepository;
import jakarta.servlet.http.HttpSession;



@Controller
public class EmergencyController {

    @Autowired
    private PatientRepository patientRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired 
    private RoomRepository roomRepo;
    @Autowired
    private EmergencyRepository emergencyRepo;
    
    @GetMapping("/admins/viewEmergency")
    public String viewEmergency(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        categorizeEmergency(model);
        return "admins/viewEmergencyPage";
    }

    @GetMapping("/admins/addEmergency")
    public String getAddEmergency(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        categorizeEmergency(model);
        return "admins/addEmergencyPage";
    }
    
    @PostMapping("/admins/addEmergency")
    public String addEmergency(Model model, @RequestParam Map<String, String> formData, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        if (formData.get("patientUsername").isBlank() || formData.get("doctorUsername").isBlank() || formData.get("severity").isBlank() || formData.get("room").isBlank()) {
            categorizeEmergency(model);
            model.addAttribute("error0", "Please enter all the form!");
            return "admins/addEmergencyPage";
        }
        String patientUsername = formData.get("patientUsername");
        String doctorUsername = formData.get("doctorUsername");
        String room = formData.get("room");
        int severity = Integer.parseInt(formData.get("severity"));

        if (patientRepo.findByUsername(patientUsername) != null) {
            categorizeEmergency(model);
            model.addAttribute("error1", "Username exist. Please choose another username!");
            return "admins/addEmergencyPage";
        }


        createPatient(patientUsername);
    
        String emerId = "E" + emergencyRepo.findAll().size();
        Emergency newEmergency = new Emergency(emerId, patientUsername, doctorUsername, room, severity);

        Room r = roomRepo.findByName(room);
        r.setAvailable(false);
        
        emergencyRepo.save(newEmergency);
        categorizeEmergency(model);
        return "admins/viewEmergencyPage";
    }

    @PostMapping("/admins/finishEmergency")
    public String finishEmergency(Model model, @RequestParam String emerId, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }

        Emergency e = emergencyRepo.findByEmerId(emerId);
        LocalDate sDate = e.getDate().toLocalDate();
        LocalTime sTime = e.getStartTime().toLocalTime();
        
        LocalDateTime sDateTime = LocalDateTime.of(sDate, sTime);
        LocalDateTime now = LocalDateTime.now().minusHours(7);

        Duration duration = Duration.between(sDateTime, now);
        int minute = (int) (duration.toMinutes());
        e.setDuration(minute);

        Room r = roomRepo.findByName(e.getRoomName());
        e.setFinish(true);
        r.setAvailable(true);

        emergencyRepo.save(e);
        roomRepo.save(r);

        categorizeEmergency(model);
        return "admins/viewEmergencyPage";
    }


    public void categorizeEmergency(Model model) {
        List<Emergency> currentEmergency = new ArrayList<>();
        List<Emergency> pastEmergency = new ArrayList<>();
        List<Emergency> emergencies = emergencyRepo.findAll();

        for (Emergency e : emergencies) {
            if (e.isFinish()) {
                pastEmergency.add(e);
            }
            else {
                currentEmergency.add(e);
            }
        }

        List<Doctor> doctors = doctorRepo.findByDepartment(Department.Emergency);
        List<Doctor> avaiDoctors = new ArrayList<>();
        for (Doctor d : doctors) {
            boolean check = false;
            for (Emergency e : currentEmergency) {
                if (e.getDoctorUsername().equals(d.getUsername())) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                avaiDoctors.add(d);
            }
        }

        List<Room> avaiRooms = roomRepo.findByIsAvailable(true);

        model.addAttribute("avaiDoctors", avaiDoctors);
        model.addAttribute("avaiRooms", avaiRooms);
        model.addAttribute("curEmergency", currentEmergency);
        model.addAttribute("pastEmergency", pastEmergency);
    }

    public void createPatient(String userName) {
        Patient p = new Patient(userName, "123456", "TBD", 20, "TBD", "TBD");
        patientRepo.save(p);
    }

}
