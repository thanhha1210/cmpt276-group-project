package cmpt276.group.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class PatientController {
    
    @Autowired
    private PatientRepository patientRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private RecordRepository recordRepo;
   

    @GetMapping("/login")
    public String getLoginPage() {
        return "loginPage";
    }
    
  
    
    @GetMapping("/patients/signup")
    public String getSignupPage() {
        return "patients/signupPage";
    }
    
    @GetMapping("/patients/home")
    public String getHome(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        model.addAttribute("patient", patient);
        return "patients/mainPage";
    }

    @GetMapping("/patients/schedule")
    public String getSchedule(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        model.addAttribute("patient", patient);
        return "patients/schedulePage";
    }

    @GetMapping("/patients/record")
    public String getRecord(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        List<Record> records = recordRepo.findByUsername(patient.getUsername());
        model.addAttribute("patient", patient);
        model.addAttribute("records", records);
        return "patients/recordPage";
    }

    @GetMapping("/patients/feedback")
    public String getFeedback(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        model.addAttribute("patient", patient);
        return "patients/feedbackPage";
    }
}
