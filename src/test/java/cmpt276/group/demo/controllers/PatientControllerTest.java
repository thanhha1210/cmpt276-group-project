// package cmpt276.group.demo.controllers;

// import static org.mockito.Mockito.when;
// import static org.hamcrest.Matchers.*;

// import java.nio.charset.Charset;
// import java.sql.Date;
// import java.sql.Time;
// import java.util.ArrayList;
// import java.util.List;

// import org.hamcrest.Matchers;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.mock.web.MockHttpSession;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

// import cmpt276.group.demo.models.Department;
// import cmpt276.group.demo.models.appointment.Appointment;
// import cmpt276.group.demo.models.appointment.AppointmentRepository;
// import cmpt276.group.demo.models.doctor.Doctor;
// import cmpt276.group.demo.models.doctor.DoctorRepository;
// import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
// import cmpt276.group.demo.models.patient.Patient;
// import cmpt276.group.demo.models.patient.PatientRepository;
// import cmpt276.group.demo.models.record.RecordRepository;
// import cmpt276.group.demo.models.schedule.Schedule;
// import cmpt276.group.demo.models.schedule.ScheduleRepository;
// import cmpt276.group.demo.models.admin.AdminRepository;

// @WebMvcTest(PatientController.class)
// @AutoConfigureMockMvc
// public class PatientControllerTest {

//     public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

//     @MockBean
//     private PatientRepository patientRepo;

//     @MockBean
//     private AppointmentRepository appointmentRepo;

//     @MockBean
//     private AdminRepository adminRepo;
    
//     @MockBean
//     private DoctorRepository doctorRepo;

//     @MockBean
//     private ScheduleRepository scheduleRepo;
    
//     @MockBean
//     private RecordRepository recordRepo;

//     @MockBean
//     private PastAppointmentRepository pastAppointmentRepo;

//     @Autowired
//     private MockMvc mockMvc;

//     @Test
//     public void testBookAppointment() throws Exception {
//         MockHttpSession mockSession = new MockHttpSession();
//         Patient p1 = new Patient("p1", "123", "patient1", 10, "123st", "123456789");
//         mockSession.setAttribute("session_patient", p1);

//         String dateStr = "2024-07-02";
//         Date date = Date.valueOf(dateStr);
//         String timeStr = "10:00:00";
//         Time time = Time.valueOf(timeStr);
//         String departmentStr = "General";
//         Department department = Department.valueOf(departmentStr);
    
//         Doctor d1 = new Doctor("d1", "123", "Doctor1", 10, "123st", "123456789", department);
//         Appointment a1 = new Appointment("doctor1", "d1", "patient1", "p1", date, time, 10, department);
        
//         Schedule s1 = new Schedule("doctor1", "d1", date, time, 10, department);
//         List<Schedule> schedules = new ArrayList<>();
//         schedules.add(s1);
//         // Mock the behavior of appointmentRepo to return the appointment
//         when(appointmentRepo.findByPatientUsername(p1.getUsername())).thenReturn(a1);
//         when(scheduleRepo.findAll()).thenReturn(schedules);
                
    
//         mockMvc.perform(MockMvcRequestBuilders.post("/patients/bookAppointment")
//             .session(mockSession)  // Pass the session to the request
//             .param("doctorUsername", d1.getUsername()) // Add necessary parameters
//             .param("date", dateStr)
//             .param("startTime", timeStr)
//             .contentType(APPLICATION_JSON_UTF8))
//             .andExpect(MockMvcResultMatchers.status().isOk())
//             .andExpect(MockMvcResultMatchers.view().name("patients/schedulePage"))
//             .andExpect(MockMvcResultMatchers.model().attribute("appointment", hasItem(
//                 allOf(
//                     hasProperty("doctorName", Matchers.is("Doctor1")),
//                     hasProperty("doctorUsername", Matchers.is("d1")),
//                     hasProperty("date", Matchers.is(date)), 
//                     hasProperty("startTime", Matchers.is(timeStr)),
//                     hasProperty("department", Matchers.is(department))
//                 )
//             )))
//             .andExpect(MockMvcResultMatchers.model().attribute("schedules", hasItem(
//                 allOf(
//                     hasProperty("doctorUsername", Matchers.is("d1")),
//                     hasProperty("date", Matchers.is(date)), 
//                     hasProperty("startTime", Matchers.is(timeStr))
//                 )
//             )))
//             ;
           
//     }       
            
    
// }
