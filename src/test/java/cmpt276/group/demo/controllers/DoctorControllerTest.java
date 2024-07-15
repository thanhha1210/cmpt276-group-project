package cmpt276.group.demo.controllers;

import cmpt276.group.demo.DemoApplication;
import cmpt276.group.demo.models.*;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.feedback.Feedback;
import cmpt276.group.demo.models.feedback.FeedbackRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// I was having trouble with this as I kept receiving this error:

// java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: 
// skipping repeated attempt to load context for [WebMergedContextConfiguration@6d7b001b testClass = cmpt276.group.demo.controllers

// which I found out had to do with WebMvc only loading a portion of the application rather than the whole

// @WebMvcTest(controllers = DoctorController.class)
// @ContextConfiguration(classes = DemoApplication.class)

@SpringBootTest // in comparison to WebMvcTest, SpringBootTest allowed me to load the full application context 
@AutoConfigureMockMvc

public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorRepository doctorRepo;

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private AppointmentRepository appointmentRepo;

    @MockBean
    private RecordRepository recordRepo;

    @MockBean
    private PastAppointmentRepository pastAppointmentRepo;

    @MockBean
    private FeedbackRepository feedbackRepo;

    // test doctor log in - POST - Valid
    @Test
    public void testValidLogin() throws Exception {
        Doctor doctor = new Doctor("d1", "123", "doc1", 20, "123St", "123", Department.General);
        when(doctorRepo.findByUsernameAndPassword("d1", "123")).thenReturn(doctor);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .sessionAttr("session_doctor", doctor)
                .param("username", "d1")
                .param("password", "123")
                .param("role", "doctor"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("doctors/mainPage"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("doctor"))
            .andExpect(MockMvcResultMatchers.model().attribute("doctor", doctor));
    }

    // test doctor log in - POST - Invalid (ie missing fields)
    @Test
    public void testInvalidLogin() throws Exception {
        Doctor doctor = new Doctor("d1", "123", "doc1", 20, "123St", "123", Department.General);
        when(doctorRepo.findByUsernameAndPassword("d1", "123")).thenReturn(doctor);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .param("username", "")      // empty field
                .param("password", "123")
                .param("role", "doctor"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("loginPage"))
            .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("doctor"));
    }

    // test doctor get dashboard - GET - doctor already log in
    @Test
    public void testValidGetDashboard() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Something st", "123-456-7890", Department.General);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/getDashboard")
                                              .sessionAttr("session_doctor", d1))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/mainPage"))
               .andExpect(model().attribute("doctor", d1));
    }

    // test doctor get dashboard - GET - doctor not log in
    @Test
    public void testInvalidGetDashboard() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Something st", "123-456-7890", Department.General);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/getDashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("loginPage"))
            .andExpect(model().attributeDoesNotExist("doctor"));
    }       

    // test doctor view record - GET - doctor already log in
    @Test
    public void testValidViewRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Dermatology);
        List<PastAppointment> pastAppointments = new ArrayList<>();
        List<Record> records = new ArrayList<>();
        when(pastAppointmentRepo.findByDoctorUsername(d1.getUsername())).thenReturn(pastAppointments);
        when(recordRepo.findByDoctorUsername(d1.getUsername())).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/viewRecord")
                                              .sessionAttr("session_doctor", d1))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/viewRecordPage"))
               .andExpect(model().attribute("doctor", d1))
               .andExpect(model().attribute("appointments", pastAppointments))
               .andExpect(model().attribute("records", records));
    }

    // test doctor view record - GET - doctor not log in
    @Test
    public void testInvalidViewRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Dermatology);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/viewRecord"))
                                              
               .andExpect(status().isOk())
               .andExpect(view().name("loginPage"))
               .andExpect(model().attributeDoesNotExist("doctor"))
               .andExpect(model().attributeDoesNotExist("records"))
               .andExpect(model().attributeDoesNotExist("appointments"));
    }

    // test doctor view schedule - GET - doctor already log in
    @Test
    public void testValidViewSchedule() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Ophthalmology);
        List<Appointment> appointments = new ArrayList<>();
        when(appointmentRepo.findByDoctorUsername(d1.getUsername())).thenReturn(appointments);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/viewSchedule")
                                              .sessionAttr("session_doctor", d1))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/viewSchedulePage"))
               .andExpect(model().attribute("doctor", d1))
               .andExpect(model().attribute("appointments", appointments));
    }

    // test doctor view schedule - GET - doctor not log in
    @Test
    public void testInvalidViewSchedule() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Ophthalmology);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/viewSchedule"))
               .andExpect(status().isOk())
               .andExpect(view().name("loginPage"))
               .andExpect(model().attributeDoesNotExist("doctor"))
               .andExpect(model().attributeDoesNotExist("appointments"));
    }

    @Test
    public void testViewFeedbackPage() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Pediatrics);
        List<Feedback> feedbackList = new ArrayList<>();
        when(feedbackRepo.findByDoctorUsername(d1.getUsername())).thenReturn(feedbackList);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/viewFeedback")
                                              .sessionAttr("session_doctor", d1))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/viewFeedbackPage"))
               .andExpect(model().attribute("doctor", d1))
               .andExpect(model().attribute("feedbackList", feedbackList));
    }

    // test doctor add record - GET 
    @Test
    public void testValidGetAddRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Cardiology);
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2024-06-27"), Time.valueOf("10:00:00"), 30, Department.Cardiology);

        when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"))).thenReturn(pastApt);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/addRecord")
                                              .sessionAttr("session_doctor", d1)
                                              .param("patientUsername", "p1")
                                              .param("doctorUsername", "d1")
                                              .param("date", "2024-06-27"))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/addRecordPage"))
               .andExpect(model().attribute("doctor", d1))
               .andExpect(model().attribute("pastApt", pastApt));
    }

    // test doctor add record - POST - case 1: SUCCESS (ie description is not empty)
    @Test
    public void testValidAddRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Cardiology);
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2024-06-27"), Time.valueOf("10:00:00"), 30, Department.Cardiology);
        Record rec1 = new Record("p1", "patient1", "d1", "doctor1", Department.Cardiology, "drink water", Date.valueOf("2024-06-27"));
        Record rec2 = new Record("p1", "patient1", "d1", "doctor1", Department.Cardiology, "drink more water", Date.valueOf("2024-06-30"));
        List<Record> records = new ArrayList<>();
        records.add(rec1);
        records.add(rec2);

        when(recordRepo.findByDoctorUsername("d1")).thenReturn(records);

        when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"))).thenReturn(pastApt);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/addRecord")
                                              .sessionAttr("session_doctor", d1)
                                              .param("patientUsername", "p1")
                                              .param("patientName", "patient1")
                                              .param("doctorUsername", "d1")
                                              .param("date", "2024-06-27")
                                              .param("description", "drink water"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/viewRecordPage"))
                .andExpect(model().attribute("doctor", d1))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("pastApt"))
                .andExpect(model().attribute("records", records))
                .andExpect(model().attributeExists("appointments"));
            verify(pastAppointmentRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"));
            verify(recordRepo, times(1)).findByDoctorUsername("d1");
            verify(pastAppointmentRepo, times(1)).findByDoctorUsername("d1");
            verify(recordRepo, times(1)).save(any(Record.class));
            verify(pastAppointmentRepo, times(1)).save(any(PastAppointment.class));
    }

    // test doctor add record - POST - case 2:  description is empty
    @Test
    public void testInvalidAddRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Cardiology);
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2024-06-27"), Time.valueOf("10:00:00"), 30, Department.Cardiology);
        Record rec1 = new Record("p1", "patient1", "d1", "doctor1", Department.Cardiology, "drink water", Date.valueOf("2024-06-27"));
        Record rec2 = new Record("p1", "patient1", "d1", "doctor1", Department.Cardiology, "drink more water", Date.valueOf("2024-06-30"));
        List<Record> records = new ArrayList<>();
        records.add(rec1);
        records.add(rec2);

        when(recordRepo.findByDoctorUsername("d1")).thenReturn(records);

        when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"))).thenReturn(pastApt);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/addRecord")
                                              .sessionAttr("session_doctor", d1)
                                              .param("patientUsername", "p1")
                                              .param("patientName", "patient1")
                                              .param("doctorUsername", "d1")
                                              .param("date", "2024-06-27")
                                              .param("description", ""))    // empty field
                .andExpect(status().is(400))
                .andExpect(view().name("doctors/addRecordPage"))
                .andExpect(model().attribute("doctor", d1))
                .andExpect(model().attributeExists("error0"))
                .andExpect(model().attributeExists("pastApt"))
                .andExpect(model().attributeDoesNotExist("records"))
                .andExpect(model().attributeDoesNotExist("appointments"));

            verify(pastAppointmentRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"));
            verify(recordRepo, times(0)).findByDoctorUsername("d1");
            verify(pastAppointmentRepo, times(0)).findByDoctorUsername("d1");
            verify(recordRepo, times(0)).save(any(Record.class));
            verify(pastAppointmentRepo, times(0)).save(any(PastAppointment.class));
    }

    @Test
    public void testExpandRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Pediatrics);
        Record record = new Record("p1", "patient1", "d1", "doctor1", Department.Cardiology, "description", Date.valueOf("2024-06-27"));

        when(recordRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"))).thenReturn(record);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/expandRecord")
                                              .sessionAttr("session_doctor", d1)
                                              .param("patientUsername", "p1")
                                              .param("doctorUsername", "d1")
                                              .param("date", "2024-06-27"))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/expandRecordPage"))
               .andExpect(model().attribute("doctor", d1))
               .andExpect(model().attribute("record", record));
    }

    // test doctor edit record - POST - case 1: success
    @Test
    public void testValidEditRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Cardiology);
        Record record = new Record("p1", "patient1", "d1", "doctor1", Department.Cardiology, "description", Date.valueOf("2024-06-27"));

        when(recordRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"))).thenReturn(record);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/editRecord")
                                              .sessionAttr("session_doctor", d1)
                                              .param("patientUsername", "p1")
                                              .param("doctorUsername", "d1")
                                              .param("date", "2024-06-27")
                                              .param("desc", "updated description"))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/expandRecordPage"))
               .andExpect(model().attribute("doctor", d1))
               .andExpect(model().attribute("record", hasProperty("description", is("updated description"))))
               .andExpect(model().attribute("success", "Change patient description successfully!"));
            verify(recordRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"));
            verify(recordRepo, times(1)).save(any(Record.class));
    }

    // test doctor edit record - POST - case 2: description is empty
    @Test
    public void testInvalidEditRecord() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Main St", "123-456-7890", Department.Cardiology);
        Record record = new Record("p1", "patient1", "d1", "doctor1", Department.Cardiology, "description", Date.valueOf("2024-06-27"));

        when(recordRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"))).thenReturn(record);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors/editRecord")
                                              .sessionAttr("session_doctor", d1)
                                              .param("patientUsername", "p1")
                                              .param("doctorUsername", "d1")
                                              .param("date", "2024-06-27")
                                              .param("desc", ""))   // empty field
               .andExpect(status().is(400))
               .andExpect(view().name("doctors/expandRecordPage"))
               .andExpect(model().attribute("doctor", d1))
               .andExpect(model().attributeExists("error0"))
               .andExpect(model().attribute("record", record))
               .andExpect(model().attributeDoesNotExist("success"));
            verify(recordRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2024-06-27"));
            verify(recordRepo, times(0)).save(any(Record.class));
    }

    // test doctor view patients' feedback - GET - case 1: doctor already log in
    @Test 
    public void testValidViewFeedback() throws Exception {
        Feedback f1 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Department.valueOf("General"), "Good doctor");
        Feedback f2 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-15"), Department.valueOf("General"), "Nice doctor");
        Doctor doc = new Doctor("d1", "123", "doctor1", 22, "123", "123", Department.General);

        List<Feedback> feedbackList = new ArrayList<>();
        feedbackList.add(f1);
        feedbackList.add(f2);

        when(feedbackRepo.findByDoctorUsername(doc.getUsername())).thenReturn(feedbackList);
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/viewFeedback")
            .sessionAttr("session_doctor", doc))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("doctors/viewFeedbackPage"))
            .andExpect(model().attribute("feedbackList", feedbackList))
            .andExpect(model().attribute("doctor", doc));

        verify(feedbackRepo, times(1)).findByDoctorUsername("d1");
    }

}
