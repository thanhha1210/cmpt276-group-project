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

import cmpt276.group.demo.models.Department;
import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

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

    // ------------------------------------------------------Get
    // Dashboard--------------------------------------------------
    @GetMapping("/admins/getDashboard")
    public String getDashboard(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        model.addAttribute("admin", admin);
        return "admins/mainPage";
    }

    // -----------------------------------------------------View & add
    // doctor-----------------------------------------------
    @GetMapping("/admins/viewDoctor")
    public String viewDoctor(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("session_admin");
        model.addAttribute("admin", admin);
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }

    @GetMapping("/admins/addDoctor")
    public String addDoctorPage(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/addDoctorPage";
    }

    @PostMapping("/admins/addDoctor")
    public String registerDoctor(@RequestParam Map<String, String> formData, HttpServletResponse response, Model model,
            HttpSession session) {
        if (formData.get("age").equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please enter all the form!");
            return "admins/addDoctorPage";
        }
        String username = formData.get("username");
        String password = formData.get("password");
        String name = formData.get("name");
        int age = Integer.parseInt(formData.get("age"));
        String address = formData.get("address");
        String phone = formData.get("phone");
        String departmentStr = formData.get("department");

        if (username.trim().equals("") || password.trim().equals("") || name.trim().equals("")
                || address.trim().equals("") || phone.trim().equals("") || departmentStr.trim().equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error0", "Please enter all the form!");
            return "admins/addDoctorPage";
        }
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
        session.setAttribute("session_doctor", newDoctor);
        model.addAttribute("success", "Add Successfully");
        return "admins/addDoctorPage";
    }

    // ------------------------------------------------------ Deletes
    // doctor--------------------------------------------------
    // admin delete doctor => all schedule & appointment with that doctor will be
    // delete
    @PostMapping("/admins/deleteDoctor")
    public String deleteDoctor(@RequestParam String username, Model model) {
        Doctor temp = doctorRepo.findByUsername(username);
        if (temp != null) {
            doctorRepo.delete(temp);
        }
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }

    // Return doctor display page
    @GetMapping("/admins/exitDoctorAdd")
    public String exitAddDoctorPage(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "admins/viewDoctorPage";
    }

    // ------------------------------------------------------ View & delete
    // appointment----------------------------------------

    // function to change appointment to past appointment
    public void changeApt() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        System.out.println(currentDate);

        // Retrieve all appointments
        List<Appointment> appointmentList = appointmentRepo.findAll();

        // Loop through appointments and update status
        for (Appointment appointment : appointmentList) {
            // if the
            if (appointment.getDate().toLocalDate().isBefore(currentDate)) {
                // Create a new PastAppointment
                PastAppointment pastAppointment = new PastAppointment(appointment.getDoctorName(),
                        appointment.getDoctorUsername(),
                        appointment.getPatientName(), appointment.getPatientUsername(),
                        appointment.getDate(), appointment.getStartTime(),
                        appointment.getDuration(), appointment.getDepartment());

                // Add to pastApt
                pastAppointmentRepo.save(pastAppointment);

                // Delete from Apt
                appointmentRepo.delete(appointment);
            }
        }
    }

    // admin view appointment
    @GetMapping("/admins/viewAppointment")
    public String viewAppointment(Model model) {
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
    public String deleteAppointment(@RequestParam Map<String, String> apt, Model model, HttpServletResponse response) {
        String doctorUsername = apt.get("doctorUsername");
        Date date = Date.valueOf(apt.get("date"));
        Time startTime = Time.valueOf(apt.get("startTime"));
        Appointment deleteApt = appointmentRepo.findByDoctorUsernameAndDateAndStartTime(doctorUsername, date,
                startTime);

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

    // ------------------------------------------------------ View, add & delete
    // schedule---------------------------------------------
    // function to change appointment to past appointment
    public void deleteSchedule() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        // Retrieve all appointments
        List<Schedule> scheduleList = scheduleRepo.findAll();

        // Loop through schedule and delete
        for (Schedule schedule : scheduleList) {
            if (schedule.getDate().toLocalDate().isBefore(currentDate)) {
                scheduleRepo.delete(schedule);
            }
        }
    }

    // go to schedule page
    @GetMapping("/admins/viewSchedule")
    public String viewSchedule(Model model) {
        // deleteSchedule();
        List<Schedule> schedules = scheduleRepo.findAll();
        Collections.sort(schedules);
        model.addAttribute("schedules", schedules);
        return "admins/viewSchedulePage";
    }

    // add button in addSchedulePage
    @GetMapping("/admins/addSchedule")
    public String addSchedulePage(Model model) {
        return "admins/addSchedulePage";
    }

    // doctor add schedule (+)
    @PostMapping("/admins/addSchedule")
    public String postMethodName(@RequestParam Map<String, String> scheduleInfo, HttpServletResponse response,
            Model model) {
        String doctorUsername = scheduleInfo.get("doctorUsername");

        // Check if any field is empty
        if (doctorUsername == null || doctorUsername.trim().isEmpty() || scheduleInfo.get("duration") == null
                || scheduleInfo.get("duration").trim().isEmpty()) {
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
                model.addAttribute("error4",
                        "The new schedule conflicts with an existing schedule. Please enter another schedule!");
                return "admins/addSchedulePage";
            }
        }

        // Check the schedule time with real time
        // LocalDate currentDate = LocalDate.now();
        // if (date.toLocalDate().isBefore(currentDate)) {
        // model.addAttribute("error5", "The schedule is before today date. Please
        // choose another date");
        // return "admins/addSchedulePage";

        // }

        // Get doctorName based on doctorUsername
        Schedule newSchedule = new Schedule(doc.getName(), doctorUsername, date, startTime, duration, department);
        scheduleRepo.save(newSchedule);
        response.setStatus(201);
        System.out.println("POGGGGGG++");
        model.addAttribute("Schedule", newSchedule);
        model.addAttribute("success", "Add Schedule Successfully");
        return "admins/addSchedulePage";
    }

    // delete button in viewSchedulePage (+)
    @PostMapping("/admins/deleteSchedule")
    public String deleteSchedule(@RequestParam Map<String, String> scheduleInfo, Model model) {
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
}
