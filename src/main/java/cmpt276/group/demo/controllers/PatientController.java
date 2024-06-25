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

import cmpt276.group.demo.models.Department;
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
    
//--------------------------------------------Manage schedule--------------------------------------------------------
    @GetMapping("/patients/viewSchedule")
    public String getSchedule(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        List<Schedule> schedules = scheduleRepo.findAll();
        Appointment apt = appointmentRepo.findByPatientName(patient.getName());
        if (apt != null) {
            model.addAttribute("appointment", apt);
        } 

        System.out.println(patient.getUsername());

        model.addAttribute("patient", patient);
        model.addAttribute("schedules", schedules);
        return "patients/schedulePage";
    }
    // Patient books appointment
    @PostMapping("/patients/bookAppointment")
    public String bookAppointment(@RequestParam Map<String, String> sche, Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        
        String doctorUsername = sche.get("doctorUsername");
        Date date = Date.valueOf(sche.get("date"));
        Time startTime = Time.valueOf(sche.get("startTime"));
        Schedule schedule = scheduleRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date, startTime);
        
        // if patient already has an appointment => change it to schedule 
        Appointment oldApt = appointmentRepo.findByPatientName(patient.getName());
        if (oldApt != null) {
            Schedule newSche = new Schedule(oldApt.getDoctorName(), oldApt.getDoctorUsername(), oldApt.getDate(), oldApt.getStartTime(), oldApt.getDuration(), oldApt.getDepartment());
            scheduleRepo.save(newSche);
            appointmentRepo.delete(oldApt);
        }

        Appointment apt = new Appointment(schedule.getDoctorName(), doctorUsername, patient.getName(), date, startTime, schedule.getDuration(), schedule.getDepartment());
        appointmentRepo.save(apt);
       
        scheduleRepo.delete(schedule);
        

        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);
        model.addAttribute("patient", patient);
        model.addAttribute("appointment", apt);
        model.addAttribute("schedules", schedules);
        return "patients/schedulePage";
    }

    @PostMapping("/patients/deleteAppointment")
    public String deleteAppointment(@RequestParam Map<String,String> apt, Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
       
        String doctorUsername = apt.get("doctorUsername");
        Date date = Date.valueOf(apt.get("date"));
        Time startTime = Time.valueOf(apt.get("startTime"));
        Appointment oldApt = appointmentRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date, startTime);

        Schedule newSche = new Schedule(oldApt.getDoctorName(), doctorUsername, date, startTime, oldApt.getDuration(), oldApt.getDepartment());
        scheduleRepo.save(newSche);
        appointmentRepo.delete(oldApt);
        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);

        model.addAttribute("patient", patient);
        // model.addAttribute("appointment", apt);
        model.addAttribute("schedules", schedules);
        return "patients/schedulePage";
    }
    
//--------------------------------------------------------------------------------------

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
