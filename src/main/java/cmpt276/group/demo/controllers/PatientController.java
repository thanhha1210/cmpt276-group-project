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
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;
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
    @Autowired 
    private ScheduleRepository scheduleRepo;
    @Autowired
    private AppointmentRepository appointmentRepo;
   
    
    @GetMapping("/patients/signup")
    public String getSignupPage() {
        return "patients/signupPage";
    }

    @GetMapping("/patients/getDashboard")
    public String getDashBoard(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        model.addAttribute("patient", patient);
        return "patients/mainPage";
    }
    

    @GetMapping("/patients/viewSchedule")
    public String getSchedule(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        
        List<Schedule> schedules = scheduleRepo.findAll();
        if (patient != null) {
            Appointment apt = appointmentRepo.findByPatientName(patient.getName());
            if (apt != null) {
                model.addAttribute("appointment", apt);
            }
        }
        model.addAttribute("patient", patient);
        model.addAttribute("schedules", schedules);
        return "patients/schedulePage";
    }

    @PostMapping("/patients/bookAppointment")
    public String bookAppointment(@RequestParam Map<String, String> sche, Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        String doctorName = sche.get("doctorName");
        String doctorUsername = sche.get("doctorUsername");
        Date date = Date.valueOf(sche.get("date"));
        Time startTime = Time.valueOf(sche.get("startTime"));
        int duration = Integer.parseInt(sche.get("duration"));
        
        Appointment apt = new Appointment(doctorName, doctorUsername, patient.getName(), date, startTime, duration);
        appointmentRepo.save(apt);
        Schedule schedule = scheduleRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date, startTime);
        if (schedule != null) {
            scheduleRepo.delete(schedule);
        }
        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);
        model.addAttribute("patient", patient);
        model.addAttribute("appointment", apt);
        model.addAttribute("schedules", schedules);
        return "patients/schedulePage";
    }
    
    @GetMapping("/patients/viewRecord")
    public String getRecord(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        List<Record> records = recordRepo.findByUsername(patient.getUsername());
        model.addAttribute("patient", patient);
        model.addAttribute("records", records);
        return "patients/recordPage";
    }

    @GetMapping("/patients/viewFeedback")
    public String getFeedback(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        model.addAttribute("patient", patient);
        return "patients/feedbackPage";
    }
}
