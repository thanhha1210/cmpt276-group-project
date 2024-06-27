package cmpt276.group.demo.controllers;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.record.Record;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class DoctorController {
  @Autowired
  private DoctorRepository doctors;

  @Autowired
  private PatientRepository patients;

  @Autowired
  private AppointmentRepository appointmentRepo;
  @Autowired
  private AdminRepository adminRepo;

  @Autowired
  private RecordRepository recordRepo;

  @Autowired
  private PastAppointmentRepository pastAppointmentRepo;

  @GetMapping("/doctors/viewRecord")
  public String viewRecord(Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);

    model.addAttribute("records",
        recordRepo.findByDoctorUserame(doctor.getUsername()));

    // List<PastAppointment> pastAppointments = pastAppointmentRepo.findAll();
    // Collections.sort(pastAppointments);

    List<Appointment> appointments = appointmentRepo.findByDoctorUsername(doctor.getUsername());
    // model.addAttribute("appointments", pastAppointments);
    model.addAttribute("appointments", appointments);
    return "/doctors/viewRecord";
  }

  @GetMapping("/doctors/getDashboard")
  public String getDashboard(Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    return "doctors/mainPage";
  }

  @GetMapping("/doctors/addRecord")
  public String addRecord(Model model, HttpSession session, @RequestParam(name = "date") String date,
      @RequestParam(name = "patientName") String patient) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    model.addAttribute("date", date);
    model.addAttribute("patient", patient);
    return "doctors/addRecord";
  }

  @PostMapping("/doctors/addRecord")
  public String addRecord(@RequestParam Map<String, String> formData, HttpServletResponse response, Model model,
      HttpSession session) {
    model.addAttribute("doctor", session.getAttribute("session_doctor"));
    // TODO: process POST request
    String description = formData.get("description");
    String dateStr = formData.get("date");
    Date date = Date.valueOf(dateStr);
    String patient = formData.get("patientName");
    String doctor = formData.get("doctorUsername");
    Record newRecord = new Record(patient, doctor, description, date);
    recordRepo.save(newRecord);
    return "doctors/addRecord";
  }

}

// @PostMapping("/admins/addDoctor")
// public String registerDoctor(@RequestParam Map<String, String> formData,
// HttpServletResponse response, Model model,
// HttpSession session) {

// String username = formData.get("username");
// String password = formData.get("password");
// String name = formData.get("name");
// int age = Integer.parseInt(formData.get("age"));
// String address = formData.get("address");
// String phone = formData.get("phone");
// String departmentStr = formData.get("department");

// Department department = Department.valueOf(departmentStr);
// Doctor newDoctor = new Doctor(username, password, name, age, address, phone,
// department);
// doctorRepo.save(newDoctor);
// response.setStatus(201);
// model.addAttribute("Doctor", newDoctor);
// session.setAttribute("session_doctor", newDoctor);
// model.addAttribute("success", "Add Successfully");
// return "admins/addDoctorPage";
// }