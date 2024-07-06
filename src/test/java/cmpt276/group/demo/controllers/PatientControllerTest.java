package cmpt276.group.demo.controllers;

import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import cmpt276.group.demo.models.Department;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /*
     *  @MockBean is an annotation provided by Spring Boot for creating mock instances of beans 
     *  within the Spring Application Context. It is typically used in integration tests or Spring Boot 
     *  tests where you want to mock a specific bean within the context of the entire application.

        Usage: It is used in tests annotated with @SpringBootTest or other Spring testing annotations.
        Scope: Mocks a Spring bean within the application context, making it available for autowiring into other beans.
        Purpose: Useful for replacing real beans with mocks within the application context during integration tests.
     */
    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private AppointmentRepository appointmentRepo;

    @MockBean
    private AdminRepository adminRepo;

    @MockBean
    private DoctorRepository doctorRepo;

    @MockBean
    private ScheduleRepository scheduleRepo;

    @MockBean
    private RecordRepository recordRepo;

    @MockBean
    private PastAppointmentRepository pastAppointmentRepo;

    /* 
     * @MockMvc is part of Spring MVC Test framework. It allows you to test your Spring MVC controllers 
     * by simulating HTTP requests and responses.

        Usage: It is used in tests where you want to test the behavior of your controllers.
        Scope: Tests the web layer, typically by sending mock HTTP requests to your controllers.
        Purpose: Useful for testing your controller logic and ensuring it handles HTTP requests and responses correctly.
     */
    @Autowired
    private MockMvc mockMvc;

    // test repository save function
   /* 
    @Test
    public void PatientRepo_SaveAll() {
        // Arrange
        Patient p1 = new Patient("p1", "123", "patient1", 10, "123st", "123456789");
        when(patientRepo.save(any(Patient.class))).thenReturn(p1);

        // Act
        Patient savePatient = patientRepo.save(p1);

        // Assert
        Assertions.assertThat(savePatient).isNotNull();
        Assertions.assertThat(savePatient.getAge()).isGreaterThan(0);
    }
    */

    // test repository get all function
    /* 
    @Test
    public void PatientRepo_GetAll() {
        // Arrange
        Patient p1 = new Patient("p1", "123", "patient1", 10, "123st", "123456789");
        Patient p2 = new Patient("p2", "123", "patient2", 10, "123st", "123456789");

        when(patientRepo.findAll()).thenReturn(Arrays.asList(p1, p2));

        // Act
        List<Patient> patientList = patientRepo.findAll();

        // Assert
        Assertions.assertThat(patientList).isNotNull();
        Assertions.assertThat(patientList.size()).isEqualTo(2);
    }
    */

    // test repository find by username function 
    /* 
    @Test
    public void PatientRepo_Return() {
        // Arrange
        Patient p1 = new Patient("p1", "123", "patient1", 10, "123st", "123456789");
       
        // In unit testing with Mockito, you need to mock the behavior of the repository methods to simulate 
        // their behavior without actually interacting with the database. 
        // This ensures that the findByUsername method returns the p1 object when called with p1.getUsername().
        when(patientRepo.findByUsername(p1.getUsername())).thenReturn(p1);

        // Act
        patientRepo.save(p1);
        Patient testPatient = patientRepo.findByUsername(p1.getUsername());

        // Assert
        Assertions.assertThat(testPatient).isNotNull();
        Assertions.assertThat(testPatient.getUsername()).isEqualTo(p1.getUsername());
    }
    */

    // 1. test patient log in


    // 2. test patient sign up


    //------------------------------------Test Appointment & Schedule--------------------------------------
    // 3. test patient view schedule - GET
    @Test
    void testGetSchedule() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.valueOf("General"));
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-28"), Time.valueOf("10:00:00"), 10, Department.valueOf("General"));
        Appointment a1 = new Appointment("doctor2", "d2", "patient1", "p1", Date.valueOf("2024-10-10"), Time.valueOf("10:00:00"), 10, Department.valueOf("Orthopedics"));
        
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);

        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(appointmentRepo.findByPatientUsername(p1.getUsername())).thenReturn(a1);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewSchedule").sessionAttr("session_patient", p1))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/schedulePage"))
                .andExpect(model().attribute("schedules", hasSize(2)))
                .andExpect(model().attribute("schedules", hasItem(allOf(
                    hasProperty("doctorUsername", is("d1")), 
                    hasProperty("date", is(s1.getDate())),
                    hasProperty("startTime", is(s1.getStartTime()))
                ))))
                .andExpect(model().attribute("appointment", allOf(
                    hasProperty("doctorUsername", is("d2")),
                    hasProperty("date", is(a1.getDate())),
                    hasProperty("startTime", is(a1.getStartTime()))
                )));
    }
        // hasItem => use for Collection

    // 4A. test patient book appointment - POST - Case patient don't have appointment 
    @Test
    void testBookAppointmentA() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.valueOf("General"));
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-28"), Time.valueOf("10:00:00"), 10, Department.valueOf("General"));

        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);

        when(scheduleRepo.findAll()).thenReturn(schedules);
       
    }

    // 4B. test patient book appointment - POST - Case patient don't have appointment 

    // 5. test patient delete appointment - POST 
  


    //------------------------------------Test Record--------------------------------------
    // 6. test patient view record - GET
    @Test 
    void testGetRecord() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Record r1 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-06-27"));
        Record r2 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-05-17"));
        Record r3 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-04-07"));

        List<Record> records = Arrays.asList(r1, r2, r3);

        when(recordRepo.findByPatientUsername(p1.getUsername())).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewRecord")
            .sessionAttr("session_patient", p1))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("patients/recordPage"))
            .andExpect(model().attribute("records", hasSize(3)))            
            ;
    }

    //------------------------------------Test Feedback------------------------------------
    
}



    

    



