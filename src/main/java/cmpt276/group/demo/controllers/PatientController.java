package cmpt276.group.demo.controllers;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import cmpt276.group.demo.models.event.EventRepository;
import cmpt276.group.demo.models.feedback.Feedback;
import cmpt276.group.demo.models.feedback.FeedbackRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;
import jakarta.servlet.http.HttpServletResponse;
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
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private FeedbackRepository feedbackRepo;
   
    
    @GetMapping("/patients/signup")
    public String getSignupPage() {
        return "patients/signupPage";
    }

    // Patient go to dashboard (added test)
    @GetMapping("/patients/getDashboard")
    public String getDashBoard(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        model.addAttribute("patient", patient);
        return "patients/mainPage";
    }
    
    //--------------------------------------------Manage schedule--------------------------------------------------------
   
    // Patient go to view schedule page (added test)
    @GetMapping("/patients/viewSchedule")
    public String getSchedule(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
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
        return "patients/viewSchedulePage";
    }
    
    // Patient books appointment  (added test)
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
        return "patients/viewSchedulePage";
    }

    // Patient view past appointments (NOT ADD TEST)
    @GetMapping("/patients/viewPastAppointment")
    public String viewPastApt(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        model.addAttribute("patient", patient);

        List<PastAppointment> pastAptList = pastAppointmentRepo.findByPatientUsername(patient.getUsername());
        List<PastAppointment> nonFeedbackPastApt = new ArrayList<>();
        for (PastAppointment apt : pastAptList) {
            if (!apt.isFeedback()) {
                nonFeedbackPastApt.add(apt);
            }
        }
        Collections.sort(nonFeedbackPastApt);
        model.addAttribute("nonFeedbackList", nonFeedbackPastApt);
        return "patients/viewPastAptPage";
    }
    

    // Patient deletes appointment (added test)
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
        return "patients/viewSchedulePage";
    }
    
    // Helper function
    // function to change appointment to past appointment (USED IN LOCALHOST)
    /*
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
    */
   

    // // function to change appointment to past appointment (USED IN LOCALHOST)
    /*
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

    */
    
    // USED ON RENDER
    public void changeApt() {
        LocalDateTime current = LocalDateTime.now().minusHours(7);

        List<Appointment> AppointmentList = appointmentRepo.findAll();

        for (Appointment appointment : AppointmentList) {
            LocalDate appointmentDate = appointment.getDate().toLocalDate();
            LocalTime appointmentTime = appointment.getStartTime().toLocalTime().plusMinutes(appointment.getDuration());
            LocalDateTime aptDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

            if (aptDateTime.isBefore(current)) {
                PastAppointment pastAppointment = 
                new PastAppointment(appointment.getDoctorName(), appointment.getDoctorUsername(), 
                                    appointment.getPatientName(), appointment.getPatientUsername(),
                                    appointment.getDate(), appointment.getStartTime(),
                                    appointment.getDuration(), appointment.getDepartment());
                
                // Add to pastAppointmentRepo
                pastAppointmentRepo.save(pastAppointment);

                // Delete from appointmentRepo
                appointmentRepo.delete(appointment);
            }
        }
    }

    // USED ON RENDER
    public void deleteSchedule() {
        LocalDateTime current = LocalDateTime.now().minusHours(7);

        List<Schedule> scheduleList = scheduleRepo.findAll();
        for (Schedule sche : scheduleList) {
            // get sche local date time
            LocalDateTime scheDateTime = LocalDateTime.of(sche.getDate().toLocalDate(), sche.getStartTime().toLocalTime());
            if (scheDateTime.isBefore(current)) {
                scheduleRepo.delete(sche);
            }
        }
    }

    //----------------------------------------Record----------------------------------------------
    // patient view record (added test)
    @GetMapping("/patients/viewRecord") 
    public String getRecord(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        List<Record> records = recordRepo.findByPatientUsername(patient.getUsername());
        Collections.sort(records);
        model.addAttribute("patient", patient);
        model.addAttribute("records", records);
        return "patients/viewRecordPage";
    }
        
    //-------------------------------------Feedback------------------------------------------------
    // @GetMapping("/patients/viewFeedback")  // added test
    // public String getFeedback(Model model, HttpSession session) {
    //     Patient patient = (Patient) session.getAttribute("session_patient");
    //     if (patient == null) {
    //         return "loginPage";
    //     }

    //     // list of past apt that are not add to feedback 
    //     List<PastAppointment> pastAptList = pastAppointmentRepo.findByPatientUsername(patient.getUsername());
    //     List<PastAppointment> nonFeedbackPastAptList = new ArrayList<>();
    //     for (PastAppointment pastApt : pastAptList) {
    //         if (!pastApt.isFeedback()) {
    //             nonFeedbackPastAptList.add(pastApt);
    //         }
    //     }

    //     Collections.sort(nonFeedbackPastAptList);
    //     model.addAttribute("nonFeedbackList", nonFeedbackPastAptList);

    //     List<Feedback> feedbackList = feedbackRepo.findAll();
    //     Collections.sort(feedbackList);
    //     model.addAttribute("feedbackList", feedbackList);
    //     return "patients/viewFeedbackPage";
    // }    

    @GetMapping("/patients/viewFeedback")   // NOT ADD TEST
    public String viewFeedback(@RequestParam String doctorUsername, HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        Doctor doctor = doctorRepo.findByUsername(doctorUsername);
        List<Feedback> feedbackList = feedbackRepo.findByDoctorUsername(doctorUsername);
        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("doctor", doctor);
        return "patients/viewFeedbackPage";
    }

    @GetMapping("/patients/addFeedback")  // added test
    public String getAddFeedbackPage(HttpSession session, Model model, @RequestParam Map<String, String> formData) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        String doctorUsername = formData.get("doctorUsername");
        String patientUsername = formData.get("patientUsername");
        Date date = Date.valueOf(formData.get("date"));
        PastAppointment pastApt = pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate(patientUsername, doctorUsername, date);
        model.addAttribute("nonFeedbackPastApt", pastApt);
        return "patients/addFeedbackPage";
    }

    @GetMapping("/patients/viewRating")     // NOT ADD TEST
    public String viewDocRating(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        List<Doctor> doctors = doctorRepo.findAll();
        Collections.sort(doctors, Comparator.comparing(Doctor::getName));
        model.addAttribute("doctors", doctors);
        for (Doctor doc : doctors) {
            System.out.println("Doctor: " + doc.getName() + ", Rate: " + doc.getRate());
        }
        return "patients/viewRatingPage";
    }

    @PostMapping("/patients/addFeedback")   // added test
    public String addFeedbackPage(@RequestParam Map<String, String> formData, HttpSession session, Model model, HttpServletResponse response) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        String doctorUsername = formData.get("doctorUsername");
        String patientUsername = formData.get("patientUsername");
        Date date = Date.valueOf(formData.get("date"));
        String feedbackStr = formData.get("feedbackStr");
        String rateStr = formData.get("rate");
        PastAppointment pastApt = pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate(patientUsername, doctorUsername, date);

        if (rateStr == null || rateStr.isEmpty() || feedbackStr.trim().isEmpty()) {
            model.addAttribute("nonFeedbackPastApt", pastApt);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please fill the feedback form!");
            return "patients/addFeedbackPage";
        }

        double rate = Double.parseDouble(rateStr);
        Doctor doc = doctorRepo.findByUsername(doctorUsername);
        
        // feedback rate is individual rate and doctor rate is the mean rate 
        Feedback newFeedback = new Feedback(doc.getName(), doctorUsername, patient.getName(), patientUsername, date, doc.getDepartment(), feedbackStr);
        newFeedback.setRate(rate);
        feedbackRepo.save(newFeedback);

        // Update the doctor's mean rating
        // feedbacks here is updated (ie added new one to repo if any)
        List<Feedback> feedbacks = feedbackRepo.findByDoctorUsername(doctorUsername);
        double mean = calcAvg(feedbacks);     // rate in parameter is new rate & update rate is avg rate
        doc.setRate(mean);
        System.out.println("mean rate: " + doc.getRate());
        doctorRepo.save(doc);

        // List<Feedback> feedbackList = feedbackRepo.findByPatientUsername(patientUsername);
        // Collections.sort(feedbackList);
        // model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("doctor", doc);

        // update past apt 
        pastApt.setIsFeedback(true);
        pastAppointmentRepo.save(pastApt);
        
        List<PastAppointment> pastAptList = pastAppointmentRepo.findByPatientUsername(patientUsername);
        List<PastAppointment> nonFeedbackPastAptList = new ArrayList<>();
        
        // display only those are not added to feedback 
        for (PastAppointment apt : pastAptList) {
            if (!apt.isFeedback()) {
                nonFeedbackPastAptList.add(apt);
            }
        }

        Collections.sort(nonFeedbackPastAptList);
        model.addAttribute("nonFeedbackList", nonFeedbackPastAptList);
        return "patients/viewPastAptPage";
    }

    public double calcAvg(List<Feedback> feedbackList) {
        // feedbackList is updated (ie added new one to it)
        double total = 0;
        for (Feedback fb : feedbackList) {
            total += fb.getRate();
        }
        int cnt = feedbackList.size();
        double mean = total / cnt;

        // Round to one decimal place
        return Math.round(mean * 10.0) / 10.0;
    }

    //---------------------------------------Edit Information-------------------------------------------

    @GetMapping("patients/editInformation")
    public String getEditPage(Model model, HttpSession session) {
        Patient p = (Patient) session.getAttribute("session_patient");
        if (p == null) {
            return "loginPage";
        }
        model.addAttribute("patient", p);

        return "patients/editInformationPage";
    }

    @PostMapping("patients/editInformation")
    public String editSetting(Model model, @RequestParam Map<String, String> formData, HttpServletResponse response, HttpSession session) {
        Patient p = (Patient) session.getAttribute("session_patient");
        if (p == null) {
            return "loginPage";
        }
        String ageStr = formData.get("age");
        String name = formData.get("name");
        String address = formData.get("address");
        String phone = formData.get("phone");
        if (name.trim().equals("") || address.trim().equals("") || phone.trim().equals("") || formData.get("age").isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please enter all the form!");
            model.addAttribute("patient", p);
            return "patients/editInformationPage";
        }
       
        int age = Integer.parseInt(formData.get("age"));
        if (age <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error1", "Please enter a valid age");
            model.addAttribute("patient", p);
            return "patients/editInformationPage";
        }


        p.setName(name);
        p.setAge(age);
        p.setAddress(address);
        p.setPhoneNumber(phone);
        patientRepo.save(p);
        response.setStatus(200);

        model.addAttribute("success", "Successfully edit information");
        model.addAttribute("patient", p);
        return "patients/editInformationPage";
    }



}
