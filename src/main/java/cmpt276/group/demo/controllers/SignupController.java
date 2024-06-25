package cmpt276.group.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class SignupController {

    @Autowired
    private PatientRepository patientRepo;
    
    @PostMapping("/patients/signup")
    public String registerPatient(@RequestParam Map<String, String> formData, HttpServletResponse response, Model model, HttpSession session) {
        if (formData.get("age").equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please enter all the form!");
            return "patients/signupPage";
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
            return "patients/signupPage";
        }
        if (age <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error1", "Please enter a valid age");
            return "patients/signupPage";
        }
        if (patientRepo.findByUsername(username) != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error2", "This username already exists. Please enter another username!");
            return "patients/signupPage";
        }

        Patient newPatient = new Patient(username, password, name, age, address, phone);
        patientRepo.save(newPatient);
        response.setStatus(201);
        session.setAttribute("session_patient", newPatient);       // ensure the patient object stored during login
        Patient patient = (Patient) session.getAttribute("session_patient");
        model.addAttribute("patient", patient);
        return "patients/mainPage";
    }
}
