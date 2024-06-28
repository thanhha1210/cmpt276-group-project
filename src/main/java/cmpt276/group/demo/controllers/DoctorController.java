package cmpt276.group.demo.controllers;

import java.sql.Date;
import java.time.LocalDate;
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
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class DoctorController {
  @Autowired
  private DoctorRepository doctorRepo;

  @Autowired
  private PatientRepository patientRepo;

  @Autowired
  private AppointmentRepository appointmentRepo;

  @Autowired
  private AdminRepository adminRepo;

  @Autowired
  private RecordRepository recordRepo;

  @Autowired
  private PastAppointmentRepository pastAppointmentRepo;

  // ---------------------------------------Dashboard-------------------------------------------------
  @GetMapping("/doctors/getDashboard")
  public String getDashboard(Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    return "doctors/mainPage";
  }

  // -------------------------------------Book appointment-------------------------------------------------

  // -------------------------------------View record-------------------------------------------------
  @GetMapping("/doctors/viewRecord")
  public String viewRecord(Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    changeApt();
    List<PastAppointment> pastAppointments = findNotRecord(doctor.getUsername());
    Collections.sort(pastAppointments);
    List<Record> records = recordRepo.findByDoctorUsername(doctor.getUsername());

    // model.addAttribute("appointments", pastAppointments);
    model.addAttribute("doctor", doctor);
    model.addAttribute("records", records);
    model.addAttribute("appointments", pastAppointments);
    return "/doctors/viewRecord";
  }

  @GetMapping("/doctors/addRecord")
  public String addRecord(@RequestParam Map<String, String> formData, Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);

    String patientUsername = formData.get("patientUsername");
    String doctorUsername = formData.get("doctorUsername");
    String dateStr = formData.get("date");
    Date date = Date.valueOf(dateStr);
    PastAppointment pastApt = pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate(patientUsername,
        doctorUsername, date);

    model.addAttribute("pastApt", pastApt);
    return "doctors/addRecord";
  }

  @PostMapping("/doctors/addRecord")
  public String addRecord(@RequestParam Map<String, String> formData, HttpServletResponse response, Model model,
      HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    // TODO: process POST request
    String description = formData.get("description");
    String patientName = formData.get("patientName");
    String patientUsername = formData.get("patientUsername");
    String doctorUsername = formData.get("doctorUsername");

    String dateStr = formData.get("date");
    Date date = Date.valueOf(dateStr);
    System.out.println(patientName);
    System.out.println(patientUsername);
    Record newRecord = new Record(patientUsername, patientName, doctorUsername, description, date);
    recordRepo.save(newRecord);

    PastAppointment pastApt = pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate(patientUsername,
        doctorUsername, date);
    pastApt.setIsReport(true);
    pastAppointmentRepo.save(pastApt);
    List<Record> records = recordRepo.findByDoctorUsername(doctor.getUsername());

    // all the list
    List<PastAppointment> pastAppointments = findNotRecord(doctorUsername);
    model.addAttribute("records", records);
    model.addAttribute("appointments", pastAppointments);

    return "doctors/viewRecord";
  }

  @GetMapping("doctors/expandRecord")
  public String expandRecord(@RequestParam Map<String, String> formData, Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);

    String patientUsername = formData.get("patientUsername");
    String doctorUsername = formData.get("doctorUsername");
    String dateStr = formData.get("date");
    Date date = Date.valueOf(dateStr);

    Record record = recordRepo.findByPatientUsernameAndDoctorUsernameAndDate(patientUsername, doctorUsername, date);

    model.addAttribute("record", record);
    return "doctors/expandRecord";
  }

  @GetMapping("/doctors/viewSchedule")
  public String viewBookedAppointments(Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    
    List<Appointment> appointments = appointmentRepo.findByDoctorUsername(doctor.getUsername());
    model.addAttribute("appointments", appointments);
    
    return "doctors/viewSchedule";
  }

  // function to get past_appointment that haven't written record
  public List<PastAppointment> findNotRecord(String doctorUsername) {
    List<PastAppointment> pastAppointmentAll = pastAppointmentRepo.findByDoctorUsername(doctorUsername);
    List<PastAppointment> pastAppointments = new ArrayList<>();
    for (int i = 0; i < pastAppointmentAll.size(); i++) {
      if (pastAppointmentAll.get(i).isReport() == false)
        pastAppointments.add(pastAppointmentAll.get(i));
    }
    return pastAppointments;
  }

  // function to change appointment to past_appointment
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

}
