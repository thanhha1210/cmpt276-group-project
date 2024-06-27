package cmpt276.group.demo.controllers;

import java.sql.Date;
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


  @GetMapping("/doctors/getDashboard")
  public String getDashboard(Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    return "doctors/mainPage";
  }

  @GetMapping("/doctors/viewRecord")
  public String viewRecord(Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);

    // List<PastAppointment> pastAppointments = pastAppointmentRepo.findAll();
    // Collections.sort(pastAppointments);

    List<Appointment> appointments = appointmentRepo.findByDoctorUsername(doctor.getUsername());
    List<Record> records = recordRepo.findByDoctorUsername(doctor.getUsername());


    // model.addAttribute("appointments", pastAppointments);\
    model.addAttribute("doctor", doctor);
    model.addAttribute("records", records);
    model.addAttribute("appointments", appointments);
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
    Appointment pastApt = appointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate(patientUsername, doctorUsername, date);
    
    model.addAttribute("pastApt", pastApt);
    return "doctors/addRecord";
  }

  @PostMapping("/doctors/addRecord")
  public String addRecord(@RequestParam Map<String, String> formData, HttpServletResponse response, Model model, HttpSession session) {
    Doctor doctor = (Doctor) session.getAttribute("session_doctor");
    model.addAttribute("doctor", doctor);
    // TODO: process POST request
    String description = formData.get("description");
    String patientUsername = formData.get("patientUsername");
    String doctorUsername = formData.get("doctorUsername");
    String patientName = formData.get("patientName");

    String dateStr = formData.get("date");
    Date date = Date.valueOf(dateStr);

    Record newRecord = new Record(patientUsername, patientName, doctorUsername, description, date);
    recordRepo.save(newRecord);


    List<Appointment> appointments = appointmentRepo.findByDoctorUsername(doctor.getUsername());
    List<Record> records = recordRepo.findByDoctorUsername(doctor.getUsername());

    

    model.addAttribute("records", records);
    model.addAttribute("appointments", appointments);

    return "doctors/viewRecord";
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