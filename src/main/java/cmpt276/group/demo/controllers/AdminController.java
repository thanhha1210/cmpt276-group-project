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

import cmpt276.group.demo.models.Department;
import cmpt276.group.demo.models.admin.Admin;
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
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private PatientRepository patientRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private AdminRepository adminRepo;
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

    // ------------------------------------------------------Get Dashboard--------------------------------------------------
   
    @GetMapping("/admins/getDashboard") 
    public String getDashboard(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        return "admins/mainPage";
    }

    // -----------------------------------------------------View & add, delete doctor-----------------------------------------------
    // admin go to view doctor page (added test)
    @GetMapping("/admins/viewDoctor")
    public String viewDoctor(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }

    @GetMapping("/admins/addDoctor")
    public String addDoctorPage(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/addDoctorPage";
    }

    @PostMapping("/admins/addDoctor")
    public String registerDoctor(@RequestParam Map<String, String> formData, HttpServletResponse response, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        String username = formData.get("username");
        String password = formData.get("password");
        String name = formData.get("name");
        String address = formData.get("address");
        String phone = formData.get("phone");
        String departmentStr = formData.get("department");

        if (username.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty()
                || address.trim().isEmpty() || phone.trim().isEmpty() || departmentStr.trim().isEmpty() || formData.get("age").isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please enter all the form!");
            return "admins/addDoctorPage";
        }
        int age = Integer.parseInt(formData.get("age"));
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

        Department department = Department.valueOf(departmentStr);
        Doctor newDoctor = new Doctor(username, password, name, age, address, phone, department);
        doctorRepo.save(newDoctor);
        response.setStatus(201);
        model.addAttribute("Doctor", newDoctor);
        model.addAttribute("success", "Add Successfully");
        return "admins/addDoctorPage";
    }
    // admin delete doctor => all schedule & appointment with that doctor will be delete
    @PostMapping("/admins/deleteDoctor")
    public String deleteDoctor(@RequestParam String username, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        Doctor temp = doctorRepo.findByUsername(username);
        List<Appointment> deleteList= appointmentRepo.findByDoctorUsername(username);
        for (int i = 0; i < deleteList.size(); i++) {
            appointmentRepo.delete(deleteList.get(i));
        }

        List<Schedule> deleteSchedule= scheduleRepo.findByDoctorUsername(username);
        for (int i = 0; i < deleteSchedule.size(); i++) {
            scheduleRepo.delete(deleteSchedule.get(i));
        }
        
        if (temp != null) {
            doctorRepo.delete(temp);
        }
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }


    // ------------------------------------------------------ View & delete patient----------------------------------------
    @GetMapping("/admins/viewPatient")
    public String viewPatient(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        model.addAttribute("patients", patientRepo.findAll());
        return "admins/viewPatientPage";
    }
    // admin delete patient => all appointment with that patient will be delete
    @PostMapping("/admins/deletePatient")
    public String deletePatient(@RequestParam String username, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        Patient temp = patientRepo.findByUsername(username);
        if (temp != null) {
            patientRepo.delete(temp);
            Appointment deleteApt = appointmentRepo.findByPatientUsername(username);
            if (deleteApt != null) {
                appointmentRepo.delete(deleteApt);
            }
        }
        model.addAttribute("patients", patientRepo.findAll());
        return "admins/viewPatientPage";
    }

    // ------------------------------------------------------ View & delete appointment----------------------------------------
   
    // admin view appointment
    @GetMapping("/admins/viewAppointment")
    public String viewAppointment(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        changeApt();
        List<Appointment> appointments = appointmentRepo.findAll();
        Collections.sort(appointments);
        List<PastAppointment> pastAppointments = pastAppointmentRepo.findAll();
        Collections.sort(pastAppointments);

        model.addAttribute("appointments", appointments);
        model.addAttribute("pastAppointments", pastAppointments);
        return "admins/viewAppointmentPage";
    }

    // admin delete appointment (+)
    @PostMapping("/admins/deleteAppointment")
    public String deleteAppointment(@RequestParam Map<String, String> apt, Model model, HttpServletResponse response, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        String doctorUsername = apt.get("doctorUsername");
        Date date = Date.valueOf(apt.get("date"));
        Time startTime = Time.valueOf(apt.get("startTime"));
        Appointment deleteApt = appointmentRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date, startTime);

        Schedule newSche = new Schedule(deleteApt.getDoctorName(), deleteApt.getDoctorUsername(), deleteApt.getDate(),
                        deleteApt.getStartTime(), deleteApt.getDuration(), deleteApt.getDepartment());
        scheduleRepo.save(newSche);

        appointmentRepo.delete(deleteApt);
        List<Appointment> appointments = appointmentRepo.findAll();
        Collections.sort(appointments);
        List<PastAppointment> pastAppointments = pastAppointmentRepo.findAll();
        Collections.sort(pastAppointments);
        
        model.addAttribute("appointments", appointments);
        model.addAttribute("pastAppointments", pastAppointments);

        return "admins/viewAppointmentPage";
    }

    // ------------------------------------------------------ View, add & delete schedule---------------------------------------------
   
    // go to schedule page
    @GetMapping("/admins/viewSchedule")
    public String viewSchedule(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        deleteSchedule();
        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);
        model.addAttribute("schedules", schedules);
        return "admins/viewSchedulePage";
    }

    // go to addSchedulePage
    @GetMapping("/admins/addSchedule")
    public String addSchedulePage(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        return "admins/addSchedulePage";
    }

    // admin add schedule (+)
    @PostMapping("/admins/addSchedule")
    public String addSchedule(@RequestParam Map<String, String> scheduleInfo, HttpServletResponse response, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        String doctorUsername = scheduleInfo.get("doctorUsername");

        // Check if any field is empty
        if (doctorUsername == null || doctorUsername.trim().isEmpty() || scheduleInfo.get("duration") == null || scheduleInfo.get("duration").trim().isEmpty() ||
            scheduleInfo.get("date") == null || scheduleInfo.get("date").trim().isEmpty() ||  scheduleInfo.get("startTime") == null || scheduleInfo.get("startTime").trim().isEmpty()) {
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
        Department department = doc.getDepartment();

        // Check if admin adds the same schedule as existing schedule
        if (scheduleRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date, startTime) != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error3", "This schedule already exists. Please enter another schedule!");
            return "admins/addSchedulePage";
        }

        // Check conflict time
        List<Schedule> sches = scheduleRepo.findByDoctorUsernameAndDate(doctorUsername, date);
        for (int i = 0; i < sches.size(); i++) {
            Schedule o = sches.get(i);
            LocalTime st = o.getStartTime().toLocalTime();
            LocalTime en = st.plusMinutes(o.getDuration());
            LocalTime newStartTime = startTime.toLocalTime();
            LocalTime newEndTime = newStartTime.plusMinutes(duration);

            if ((st.isAfter(newStartTime) && st.isBefore(newEndTime))
                    || (newStartTime.isAfter(st) && newStartTime.isBefore(en))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                model.addAttribute("error4", "The new schedule conflicts with an existing schedule. Please enter another schedule!");
                return "admins/addSchedulePage";
            }
        }
        // USED IN LOCALHOST
        /* 
        // Check the schedule time with real time 
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        if (date.toLocalDate().isBefore(currentDate) || (date.toLocalDate().isEqual(currentDate) && startTime.toLocalTime().isBefore(currentTime))) {
            model.addAttribute("error5", "The schedule is behind the current date and time. Please enter a valid schedule");
            return "admins/addSchedulePage";   
        }
        */

        // ---------------------------------------------USE LOCAL DATE TIME (ON RENDER)--------------------------------------------------//
        // check schedule time with current time 
        LocalDateTime current = LocalDateTime.now().minusHours(7);
        LocalDateTime scheDateTime = LocalDateTime.of(date.toLocalDate(), startTime.toLocalTime());
        if (scheDateTime.isBefore(current)) {
            model.addAttribute("error5", "The schedule is behind the current date and time. Please enter a valid schedule");
            return "admins/addSchedulePage";
        }
        // ------------------------------------------------------------------------------------------------------------------------------//

        // check if new sche conflict w/ existing doc's apt
        List<Appointment> docAptList = appointmentRepo.findByDoctorUsername(doctorUsername);

        // loop thru doc's apt list
        for (Appointment apt : docAptList) {

            // check new sche if it conflict w/ date time of apts in doc's apt
            LocalTime newScheStartTime = startTime.toLocalTime();
            LocalTime newScheEndTime = newScheStartTime.plusMinutes(duration);

            // Get apt date and time in doc's apt list
            LocalTime aptStartTime = apt.getStartTime().toLocalTime();
            LocalTime aptEndTime = aptStartTime.plusMinutes(apt.getDuration());

            if ((newScheStartTime.isBefore(aptStartTime) && newScheEndTime.isAfter(aptStartTime)) || 
                (newScheStartTime.isAfter(aptStartTime) && newScheStartTime.isBefore(aptEndTime))) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                model.addAttribute("error6", "Schedule conflict with doctor's existing appointment. Please enter a valid schedule");
                return "admins/addSchedulePage";
            }
        }

        // Get doctorName based on doctorUsername
        Schedule newSchedule = new Schedule(doc.getName(), doctorUsername, date, startTime, duration, department);
        scheduleRepo.save(newSchedule);
        response.setStatus(201);
        model.addAttribute("schedule", newSchedule);
        model.addAttribute("success", "Add Schedule Successfully");
        return "admins/addSchedulePage";
    }

    // delete button in viewSchedulePage (+)
    @PostMapping("/admins/deleteSchedule")
    public String deleteSchedule(@RequestParam Map<String, String> scheduleInfo, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        String doctorUsername = scheduleInfo.get("doctorUsername");
        Date date = Date.valueOf(scheduleInfo.get("date"));
        Time startTime = Time.valueOf(scheduleInfo.get("startTime"));

        Schedule deleteSchedule = scheduleRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date, startTime);
        scheduleRepo.delete(deleteSchedule);

        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);
        model.addAttribute("schedules", schedules);
        return "admins/viewSchedulePage";
    }


    // ------------------------------------------------------ View, add & delete event---------------------------------------------
    // go to view event page
    @GetMapping("/admins/viewEvent")
    public String viewEvent(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        autoChangeEvent();
        categorizeEvent(model);
        return "admins/viewEventPage";
    }

    // go to add event page
    @GetMapping("/admins/addEvent") 
    public String addEventPage(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        return "admins/addEventPage";
    }
    
    // add event
    @PostMapping("/admins/addEvent")
    public String addEvent(@RequestParam Map<String, String> formData, Model model, HttpServletResponse response, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        //TODO: process POST request
      
        if (formData.get("startTime").trim().isEmpty() || formData.get("date").trim().isEmpty() ||formData.get("eventCode").trim().isEmpty() || formData.get("eventName").trim().isEmpty() || 
            formData.get("capacity").trim().isEmpty()  ||formData.get("duration").trim().isEmpty() || formData.get("description").trim().isEmpty() ) {
            model.addAttribute("error0", "Please fill all the forms");
            return "admins/addEventPage";
        }

        String eventCode = formData.get("eventCode");
        String eventName = formData.get("eventName");
        String description = formData.get("description");

        Date date = Date.valueOf(formData.get("date"));
        Time startTime = Time.valueOf(formData.get("startTime")+ ":00");
        int capacity = Integer.parseInt(formData.get("capacity"));
        int  duration = Integer.parseInt(formData.get("duration"));
        
        if (eventRepo.findByEventCode(eventCode) != null) {
            model.addAttribute("error1", "This event code existed");
            return "admins/addEventPage";
        }

        Event newEvent = new Event(eventCode, eventName, capacity, description, date, startTime, duration);

        if (!checkValidEvent(newEvent)) {
            model.addAttribute("error2", "The event is behind the current date and time. Please enter a valid time for event");
            return "admins/addEventPage";
        }
        eventRepo.save(newEvent);
        model.addAttribute("success", "You create an event successfully");
        categorizeEvent(model);


        

        return "admins/addEventPage";
    }

    // delete event
    @PostMapping("/admins/deleteEvent")
    public String deleteEvent(@RequestParam String eventCode, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        Event deleteEvent = eventRepo.findByEventCode(eventCode); 
         
        if (deleteEvent != null) {
            eventRepo.delete(deleteEvent);
        }
       
        categorizeEvent(model);
        return "admins/viewEventPage";
    }
    
    // display event
    @GetMapping("/admins/displayEvent")
    public String displayEvent(@RequestParam String eventCode, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        Event displayEvent = eventRepo.findByEventCode(eventCode);
        model.addAttribute("event", displayEvent);
        return "admins/displayEventPage";
    }

     // display pass event
     @GetMapping("/admins/displayPassEvent")
     public String displayPassEvent(@RequestParam String eventCode, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        Event displayEvent = eventRepo.findByEventCode(eventCode);
        model.addAttribute("event", displayEvent);
        return "admins/displayPassEventPage";
     }

    // edit event
    @PostMapping("/admins/editEvent")
    public String editEvent(@RequestParam Map<String, String> formData, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        if (admin == null) {
            return "loginPage";
        }
        Event editEvent = eventRepo.findByEventCode(formData.get("eventCode"));
        String description = formData.get("description");
        if (description.trim().isEmpty()) {
            model.addAttribute("error0", "Please enter description form");
        }
        else {
            editEvent.setDescription(formData.get("description"));
            eventRepo.save(editEvent);
            model.addAttribute("success", "Successfully edit event");
        }
        
        model.addAttribute("event", editEvent);
        return "admins/displayEventPage";

    }

    // helper function to check event
    public boolean checkValidEvent(Event event) {
        // check schedule time with current time 
        //LocalDateTime current = LocalDateTime.now();
       
        LocalDateTime current = LocalDateTime.now().minusHours(7);
        LocalDateTime evenDateTime = LocalDateTime.of(event.getDate().toLocalDate(), event.getStartTime().toLocalTime());
        if (evenDateTime.isBefore(current)) {
            return false;
        }
        return true;
    }

    // helper function to check pass
    public void autoChangeEvent() {
        // check schedule time with current time 
        List<Event> events = eventRepo.findAll();
        //LocalDateTime current = LocalDateTime.now();
        LocalDateTime current = LocalDateTime.now().minusHours(7);
      

        for (Event event : events) {
            LocalDateTime evenDateTime = LocalDateTime.of(event.getDate().toLocalDate(), event.getStartTime().toLocalTime());
            if (evenDateTime.isBefore(current)) {
                event.setPast(true);
                eventRepo.save(event);
            }
        }
   }

   // helper function to caegorize event list
   public void categorizeEvent(Model model) {
        List<Event> unpassEvents = new ArrayList<>();
        List<Event> passEvents = new ArrayList<>();
        List<Event> events = eventRepo.findAll();

        for (Event e : events) {
            if (e.isPast()) {
                passEvents.add(e);
            }
            else {
                unpassEvents.add(e);
            }
        }
        Collections.sort(passEvents);
        Collections.sort(unpassEvents);
        model.addAttribute("passEvents", passEvents);
        model.addAttribute("unpassEvents", unpassEvents);
   }

    // ----------------------------------------------View all patients' feedback------------------------------------ 
    @GetMapping("/admins/viewFeedback") // added test
    public String getMethodName(Model model) {
        // view all feedback
        List<Feedback> feedbackList = feedbackRepo.findAll();
        Collections.sort(feedbackList);
        model.addAttribute("feedbackList", feedbackList);
        return "admins/viewFeedbackPage";
    }
    

    // ----------------------------------------------Helper function----------------------------------------------
    // function to change appointment to past appointment
    // USED IN LOCALHOST
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

    // function to change appointment to past appointment
    // USED IN LOCALHOST
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
}
