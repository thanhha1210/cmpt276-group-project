package cmpt276.group.demo.controllers;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
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
    @Autowired
    private PastAppointmentRepository pastAppointmentRepo;
   
    
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
   
    // Patient go to view schedule page
    @GetMapping("/patients/viewSchedule")
    public String getSchedule(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        deleteSchedule();
        changeApt();
        String patientUsername = patient.getUsername();

        Appointment apt = appointmentRepo.findByPatientUsername(patientUsername);
        if (apt != null) {
            model.addAttribute("appointment", apt);
        }

        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);
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
        String patientUsername = patient.getUsername();
        
        // if patient already has an appointment => change it to schedule 
        Appointment oldApt = appointmentRepo.findByPatientUsername(patientUsername);
        if (oldApt != null) {
            Schedule newSche = new Schedule(oldApt.getDoctorName(), oldApt.getDoctorUsername(), oldApt.getDate(), oldApt.getStartTime(), oldApt.getDuration(), oldApt.getDepartment());
            scheduleRepo.save(newSche);
            appointmentRepo.delete(oldApt);
        }

        Appointment apt = new Appointment(schedule.getDoctorName(), doctorUsername, patient.getName(), patientUsername, date, startTime, schedule.getDuration(), schedule.getDepartment());
        appointmentRepo.save(apt);

        scheduleRepo.delete(schedule);
        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);
        model.addAttribute("patient", patient);
        model.addAttribute("appointment", apt);
        model.addAttribute("schedules", schedules);
        return "patients/schedulePage";
    }

    // Patient deletes appointment
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
        model.addAttribute("schedules", schedules);
        return "patients/schedulePage";
    }
    
//--------------------------------------------------------------------------------------

    @GetMapping("/patients/viewRecord")
    public String getRecord(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        List<Record> records = recordRepo.findByPatientUsername(patient.getUsername());
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

    
    // Helper function
    // function to change appointment to past appointment
    public void deleteSchedule() {
        // Get the current date and time 
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Retrieve all appointments
        List<Schedule> scheduleList = scheduleRepo.findAll();

        // Loop through schedule and delete if pass the current time
        for (Schedule schedule : scheduleList) {

            // Get date and time of each schedule in a list
            LocalDate scheDate = schedule.getDate().toLocalDate();
            LocalTime scheTime = schedule.getStartTime().toLocalTime();
            if (scheDate.isBefore(currentDate) || (scheDate.isEqual(currentDate) && scheTime.isBefore(currentTime))) {
                scheduleRepo.delete(schedule);
            }
        }
    }

    // function to change appointment to past appointment
    public void changeApt() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        System.out.println(currentDate);
        System.out.println(currentTime);
        // Retrieve all appointments
        List<Appointment> appointmentList = appointmentRepo.findAll();

        // Loop through appointments and update status
        for (Appointment apt : appointmentList) {
            
            // Get date and startTime of each appointment in a list
            LocalDate aptDate = apt.getDate().toLocalDate();
            LocalTime aptTime = apt.getStartTime().toLocalTime().plusMinutes(apt.getDuration());
            System.out.println(aptDate);
            System.out.println(aptTime);
            if (aptDate.isBefore(currentDate) || (aptDate.isEqual(currentDate) && aptTime.isBefore(currentTime))) {

                // Create a new PastAppointment
                PastAppointment pastApt = 
                new PastAppointment(apt.getDoctorName(), apt.getDoctorUsername(),
                                    apt.getPatientName(), apt.getPatientUsername(),
                                    apt.getDate(), apt.getStartTime(),
                                    apt.getDuration(), apt.getDepartment());

                // Add to pastApt
                pastAppointmentRepo.save(pastApt);

                // Delete from Apt
                appointmentRepo.delete(apt);
            }
        }
    }

    @GetMapping("/patients/recordPage")
    public String viewPastAppointments(Model model, HttpSession session) {
        String username = (String) session.getAttribute("session_user");
  
        if (username != null && !username.isEmpty()) {
            List<Record> records = recordRepo.findByPatientUsername(username);
            Collections.sort(records);
            model.addAttribute("records", records);
        } 
        else {
            model.addAttribute("error", "No user logged in");
        }
        return "patients/recordPage";
    }
}
