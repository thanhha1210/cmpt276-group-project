package cmpt276.group.demo.controllers;

import cmpt276.group.demo.DemoApplication;
import cmpt276.group.demo.models.*;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.feedback.Feedback;
import cmpt276.group.demo.models.feedback.FeedbackRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
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

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
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

    @Test
    public void testGetDashboard() throws Exception {
        Doctor d1 = new Doctor("d1", "password", "doctor1", 40, "123 Something st", "123-456-7890", Department.General);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/doctors/getDashboard")
                                              .sessionAttr("session_doctor", d1))
               .andExpect(status().isOk())
               .andExpect(view().name("doctors/mainPage"))
               .andExpect(model().attribute("doctor", d1));
    }

    @Test
    public void testViewRecord() throws Exception {
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

    @Test
    public void testViewSchedule() throws Exception {
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

    @Test
    public void testAddRecord() throws Exception {
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

    @Test
    public void testEditRecord() throws Exception {
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
    }
}
