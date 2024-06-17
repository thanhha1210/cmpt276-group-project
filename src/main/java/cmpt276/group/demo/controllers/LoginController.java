package cmpt276.group.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private PatientRepository patientRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    
    @GetMapping("/users/login")
    public String getLogin(Model model, HttpServletRequest request, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient != null) {
            model.addAttribute("patient", patient);
            return "patients/mainPage";
        }

        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin != null) {
            model.addAttribute("admin", admin);
            return "admins/mainPage";
            //return "patients/mainPage";
        }

        Doctor doctor = (Doctor) session.getAttribute("session_doctor");
        if (doctor != null) {
            model.addAttribute("doctor", doctor);
            return "doctors/mainPage";
        }
        return "loginPage";
    }
    
    @PostMapping("/users/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
        String name = formData.get("username");
        String pwd = formData.get("password");
        String option = formData.get("role");
        if (option != null) {
            switch (option) {
                case "patient": {
                    Patient patient = patientRepo.findByUsernameAndPassword(name, pwd);
                    if (patient == null) {
                        return "loginPage";
                    } 
                    else {
                        session.setAttribute("session_patient", patient);       // ensure the patient object stored during login
                        model.addAttribute("patient", patient);
                        return "patients/mainPage";
                    }
                }
                case "admin" : {
                    Admin admin = adminRepo.findByUsernameAndPassword(name, pwd);
                    if (admin == null) {
                        return "loginPage";
                    } 
                    else {
                        session.setAttribute("session_admin", admin);       // ensure the admin object stored during login
                        model.addAttribute("admin", admin);
                        return "admins/mainPage";
                    }
                }
                case "doctor": {
                    Doctor doctor = doctorRepo.findByUsernameAndPassword(name, pwd);
                    if (doctor == null) {
                        return "loginPage";
                    } 
                    else {
                        session.setAttribute("session_doctor", doctor);       // ensure the doctor object stored during login
                        model.addAttribute("doctor", doctor);
                        return "doctors/mainPage";
                    }
                }    
            }
        }
        return "loginPage";
    }
    
    @GetMapping("/users/logout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/index.html";
    }

}
