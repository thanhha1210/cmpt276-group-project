package cmpt276.group.demo.controllers;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
import cmpt276.group.demo.models.event.Event;
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
    // public void deleteSchedule() {
    //     // Get the current date and time 
    //     LocalDate currentDate = LocalDate.now();
    //     LocalTime currentTime = LocalTime.now();

    //     // Retrieve all appointments
    //     List<Schedule> scheduleList = scheduleRepo.findAll();

    //     // Loop through schedule and delete if pass the current time
    //     for (Schedule schedule : scheduleList) {

    //         // Get date and time of each schedule in a list
    //         LocalDate scheDate = schedule.getDate().toLocalDate();
    //         LocalTime scheTime = schedule.getStartTime().toLocalTime();
    //         if (scheDate.isBefore(currentDate) || (scheDate.isEqual(currentDate) && scheTime.isBefore(currentTime))) {
    //             scheduleRepo.delete(schedule);
    //         }
    //     }
    // }

    // // function to change appointment to past appointment (USED IN LOCALHOST)
    // public void changeApt() {
    //     // Get the current date
    //     LocalDate currentDate = LocalDate.now();
    //     LocalTime currentTime = LocalTime.now();
    //     System.out.println(currentDate);
    //     System.out.println(currentTime);
    //     // Retrieve all appointments
    //     List<Appointment> appointmentList = appointmentRepo.findAll();

    //     // Loop through appointments and update status
    //     for (Appointment apt : appointmentList) {
            
    //         // Get date and startTime of each appointment in a list
    //         LocalDate aptDate = apt.getDate().toLocalDate();
    //         LocalTime aptTime = apt.getStartTime().toLocalTime().plusMinutes(apt.getDuration());
    //         System.out.println(aptDate);
    //         System.out.println(aptTime);
    //         if (aptDate.isBefore(currentDate) || (aptDate.isEqual(currentDate) && aptTime.isBefore(currentTime))) {

    //             // Create a new PastAppointment
    //             PastAppointment pastApt = 
    //             new PastAppointment(apt.getDoctorName(), apt.getDoctorUsername(),
    //                                 apt.getPatientName(), apt.getPatientUsername(),
    //                                 apt.getDate(), apt.getStartTime(),
    //                                 apt.getDuration(), apt.getDepartment());

    //             // Add to pastApt
    //             pastAppointmentRepo.save(pastApt);

    //             // Delete from Apt
    //             appointmentRepo.delete(apt);
    //         }
    //     }
    // }

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
    
    //-------------------------------------Event------------------------------------------------
    // go to patient event view
    @GetMapping("/patients/viewEvent")
    public String getEvent(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        List<Event> bookedEvent = new ArrayList<>();
        List<Event> unBookedEvent = new ArrayList<>();
        categorizeList(patient.getUsername(), bookedEvent, unBookedEvent);
        
        model.addAttribute("booked", bookedEvent);
        model.addAttribute("unbooked", unBookedEvent);
        return "patients/viewEventPage";
    }

    // book an event
    @PostMapping("/patients/addEvent")
    public String addEvent(@RequestParam String eventCode, Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        Event event = eventRepo.findByEventCode(eventCode);
        if (event.getCurrentNum() < event.getCapacity()) {
            patient.getEventsJoin().add(event.getEventCode());
            event.getPatients().add(patient.getUsername());
            event.setCurrentNum(event.getPatients().size());
    
            patientRepo.save(patient); // Save patient to update the join table
            eventRepo.save(event); // Save event to update the current number
        }

        List<Event> bookedEvent = new ArrayList<>();
        List<Event> unBookedEvent = new ArrayList<>();
        categorizeList(patient.getUsername(), bookedEvent, unBookedEvent);
        
        model.addAttribute("booked", bookedEvent);
        model.addAttribute("unbooked", unBookedEvent);
        return "patients/viewEventPage";
    }


    // delete an booked event
    @PostMapping("/patients/deleteEvent")
    public String deleteEvent(@RequestParam String eventCode, Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        Event event = eventRepo.findByEventCode(eventCode);
        patient.getEventsJoin().remove(event.getEventCode());
        event.getPatients().remove(patient.getUsername());
        event.setCurrentNum(event.getPatients().size());

        patientRepo.save(patient); // Save patient to update the join table
        eventRepo.save(event); // Save event to update the current number
       
        List<Event> bookedEvent = new ArrayList<>();
        List<Event> unBookedEvent = new ArrayList<>();
        categorizeList(patient.getUsername(), bookedEvent, unBookedEvent);
        
        model.addAttribute("booked", bookedEvent);
        model.addAttribute("unbooked", unBookedEvent);
        return "patients/viewEventPage";
    }
    
    // helper categorizeList
    public void categorizeList(String username, List<Event> bookedEvent, List<Event> unBookedEvent) {
        List<Event> events = eventRepo.findAll();
        for (Event e : events) {
            boolean check = false;
            for (String name : e.getPatients()) {
                if (name.equals(username)) {
                    bookedEvent.add(e);
                    check = true;
                    break;
                }
            }
            if (!check) {
                unBookedEvent.add(e);
            }
          
        }
    }
    
    //-------------------------------------Feedback------------------------------------------------
     @GetMapping("/patients/viewFeedback")
    public String getFeedback(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        // list of past apt that are not add to feedback 
        List<PastAppointment> pastAptList = pastAppointmentRepo.findByPatientUsername(patient.getUsername());
        List<PastAppointment> nonFeedbackPastAptList = new ArrayList<>();
        for (PastAppointment pastApt : pastAptList) {
            if (!pastApt.isFeedback()) {
                nonFeedbackPastAptList.add(pastApt);
            }
        }

        Collections.sort(nonFeedbackPastAptList);
        model.addAttribute("nonFeedbackList", nonFeedbackPastAptList);

        List<Feedback> feedbackList = feedbackRepo.findAll();
        Collections.sort(feedbackList);
        model.addAttribute("feedbackList", feedbackList);
        return "patients/viewFeedbackPage";
    }    

    @GetMapping("/patients/addFeedback")
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
    

    @PostMapping("/patients/addFeedback")
    public String addFeedbackPage(@RequestParam Map<String, String> formData, HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        String doctorUsername = formData.get("doctorUsername");
        String patientUsername = formData.get("patientUsername");
        Date date = Date.valueOf(formData.get("date"));
        String feedbackStr = formData.get("feedbackStr");
        PastAppointment pastApt = pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate(patientUsername, doctorUsername, date);

        if (feedbackStr.trim().isEmpty()) {
            model.addAttribute("nonFeedbackPastApt", pastApt);
            model.addAttribute("error0", "Please fill the feedback form!");
            return "patients/addFeedbackPage";
        }

        // public Feedback(String doctorName, String doctorUsername, String patientName, String patientUsername, Date date, Department department, String feedback)
        Doctor doc = doctorRepo.findByUsername(doctorUsername);
        Feedback newFeedback = new Feedback(doc.getName(), doctorUsername, patient.getName(), patientUsername, date, doc.getDepartment(), feedbackStr);
        feedbackRepo.save(newFeedback);
        List<Feedback> feedbackList = feedbackRepo.findByPatientUsername(patientUsername);
        Collections.sort(feedbackList);
        model.addAttribute("feedbackList", feedbackList);

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
        return "patients/viewFeedbackPage";
    }



}
