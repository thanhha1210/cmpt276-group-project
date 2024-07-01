package cmpt276.group.demo.controllers;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.ScheduleRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;

@RunWith(SpringRunner.class)
@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc
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

    // test repository get all function
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

    // test repository find by username function 
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
    
    // Example
    /*
    @Test
    public void testGetPatient() throws Exception {
        // Arrange
        Patient p1 = new Patient("p1", "123", "patient1", 10, "123st", "123456789");
        when(patientRepo.findByUsername(p1.getUsername())).thenReturn(p1);

        // Act & Assert
        mockMvc.perform(get("/patients/{username}", p1.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(p1.getUsername()));
    }
    */
    


}
