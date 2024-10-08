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
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cmpt276.group.demo.DemoApplication;
import cmpt276.group.demo.models.Department;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.event.EventRepository;
import cmpt276.group.demo.models.feedback.Feedback;
import cmpt276.group.demo.models.feedback.FeedbackRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.Record;
import cmpt276.group.demo.models.record.RecordRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;

@WebMvcTest(controllers = {PatientController.class, LoginController.class, SignupController.class})     // import multiple controllers if needed
@ContextConfiguration(classes = DemoApplication.class)
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
    
    // all autowired repo in PatientController has to be mock
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

    @MockBean
    private EventRepository eventRepo;
    
    @MockBean
    private FeedbackRepository feedbackRepo;

    /* 
     * @MockMvc is part of Spring MVC Test framework. It allows you to test your Spring MVC controllers 
     * by simulating HTTP requests and responses.

        Usage: It is used in tests where you want to test the behavior of your controllers.
        Scope: Tests the web layer, typically by sending mock HTTP requests to your controllers.
        Purpose: Useful for testing your controller logic and ensuring it handles HTTP requests and responses correctly.
     */
    @Autowired
    private MockMvc mockMvc;

    //------------------------------------Test Signup--------------------------------------
    // test patient sign up - POST - SUCCESS 
    @Test
    public void testValidSignup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/patients/signup")
            // not sign in yet => no session
            .param("username", "p1")
            .param("password", "123456")
            .param("name", "patient1")
            .param("age", "20")
            .param("address", "123")
            .param("phone", "123"))
            .andExpect(MockMvcResultMatchers.status().is(201))
            .andExpect(MockMvcResultMatchers.view().name("patients/mainPage"))
            .andExpect(model().attributeExists("patient"))
            .andExpect(model().attributeDoesNotExist("error0"))
            .andExpect(model().attributeDoesNotExist("error1"))
            .andExpect(model().attributeDoesNotExist("error2"));

        verify(patientRepo, times(1)).save(any(Patient.class));
        verify(patientRepo, times(1)).findByUsername("p1");
    }

    // test patient sign up - POST - INVALID: empty fields 
    @Test
    public void testInvalidSignup1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/patients/signup")
            // not sign in yet => no session
            .param("username", "")      // empty
            .param("password", "123")
            .param("name", "patient1")
            .param("age", "20")
            .param("address", "")       // empty
            .param("phone", "123"))
            .andExpect(MockMvcResultMatchers.status().is(400))
            .andExpect(MockMvcResultMatchers.view().name("patients/signupPage"))
            .andExpect(model().attributeDoesNotExist("patient"))
            .andExpect(model().attributeExists("error0"))
            .andExpect(model().attributeDoesNotExist("error1"))
            .andExpect(model().attributeDoesNotExist("error2"));

        verify(patientRepo, times(0)).save(any(Patient.class));
        verify(patientRepo, times(0)).findByUsername("p1");
    }

    // test patient sign up - POST - INVALID: age <= 0
    @Test
    public void testInvalidSignup2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/patients/signup")
            // not sign in yet => no session
            .param("username", "p1")      
            .param("password", "123")
            .param("name", "patient1")
            .param("age", "-20")
            .param("address", "123")       
            .param("phone", "123"))
            .andExpect(MockMvcResultMatchers.status().is(400))
            .andExpect(MockMvcResultMatchers.view().name("patients/signupPage"))
            .andExpect(model().attributeDoesNotExist("patient"))
            .andExpect(model().attributeExists("error1"))
            .andExpect(model().attributeDoesNotExist("error0"))
            .andExpect(model().attributeDoesNotExist("error2"));

        verify(patientRepo, times(0)).save(any(Patient.class));
        verify(patientRepo, times(0)).findByUsername("p1");
    }

    // test patient sign up - POST - INVALID: username already exists
    @Test
    public void testInvalidSignup3() throws Exception {
        Patient patient = new Patient("p1", "123", "patient1", 18, "123", "123");
        when(patientRepo.findByUsername("p1")).thenReturn(patient);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/signup")
        // not sign in yet => no session
            .param("username", "p1")      
            .param("password", "123")
            .param("name", "patient1")
            .param("age", "20")
            .param("address", "123")       
            .param("phone", "123"))
            .andExpect(MockMvcResultMatchers.status().is(400))
            .andExpect(MockMvcResultMatchers.view().name("patients/signupPage"))
            .andExpect(model().attributeDoesNotExist("patient"))
            .andExpect(model().attributeExists("error2"))
            .andExpect(model().attributeDoesNotExist("error0"))
            .andExpect(model().attributeDoesNotExist("error1"));

        verify(patientRepo, times(0)).save(any(Patient.class));
        verify(patientRepo, times(1)).findByUsername("p1");
    }

    // ------------------------------------Test Login--------------------------------------
    // 0A. test patient log in - POST - Valid
    @Test
    public void testValidLogin() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        when(patientRepo.findByUsernameAndPassword("p1", "123")).thenReturn(p1);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .sessionAttr("session_patient", p1)
                .param("username", "p1")
                .param("password", "123")
                .param("role", "patient"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("patients/mainPage"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("patient"))
            .andExpect(MockMvcResultMatchers.model().attribute("patient", p1));
    }

    // 0B. test patient log in - POST - Invalid (ie missing fields)
    @Test
    public void testInvalidLogin1() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        when(patientRepo.findByUsernameAndPassword("p1", "123")).thenReturn(p1);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .param("username", "p1")
                .param("password", "")      // empty field
                .param("role", ""))         // empty field
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("loginPage"))
            .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("patient"));
    }

    // 0C. test patient log in - POST - Invalid (ie wrong username or password)
    @Test
    public void testInvalidLogin2() throws Exception {
        when(patientRepo.findByUsernameAndPassword("p2", "123")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .param("username", "p2")
                .param("password", "123")
                .param("role", "patient"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("loginPage"))
            .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("patient"));
    }

    //------------------------------------Test getDashboard--------------------------------------
    // 1A. test patient view dashboard - GET - Valid
    @Test
    public void testValidViewDashboard() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/getDashboard")
                                                .sessionAttr("session_patient", p1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/mainPage"));
    }

    // 1B. test patient view dashboard - GET - InValid
    @Test
    public void testInvalidViewDashboard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/getDashboard")) // no session_patient
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("loginPage"));
    }

    //------------------------------------Test Schedule--------------------------------------
    // Note: hasItem => use for Collection
    // 1A. test patient view schedule - GET - Case 1 : valid
    @Test
    public void testValidViewSchedule() throws Exception {
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
                .andExpect(MockMvcResultMatchers.view().name("patients/viewSchedulePage"))
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

    // 1B. test patient view schedule - GET - Case 2: hasn't login
    @Test
    public void testInvalidViewSchedule() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.valueOf("General"));
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-28"), Time.valueOf("10:00:00"), 10, Department.valueOf("General"));
        Appointment a1 = new Appointment("doctor2", "d2", "patient1", "p1", Date.valueOf("2024-10-10"), Time.valueOf("10:00:00"), 10, Department.valueOf("Orthopedics"));
        
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);

        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(appointmentRepo.findByPatientUsername(p1.getUsername())).thenReturn(a1);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewSchedule"))
               .andExpect(MockMvcResultMatchers.view().name("loginPage"))
        ;
    }


    // 2A. test patient book appointment - POST - Case 1 : patient have appointment 
    @Test
    public void testValidBookAppointment1() throws Exception {
        // Setup patient and schedule data
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);
    
        // Mock repository methods
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"))).thenReturn(s1);
    
        // Mock old appointment case
        Appointment oldApt = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        when(appointmentRepo.findByPatientUsername("p1")).thenReturn(oldApt);
    
        // Create a new schedule that will represent the updated schedule list after booking
        Schedule s3 = new Schedule("doctor1", "d1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        List<Schedule> updatedSchedules = new ArrayList<>();
        updatedSchedules.add(s2);
        updatedSchedules.add(s3);
    
        // Mock repository findAll() to return the updated list of schedules after booking
        when(scheduleRepo.findAll()).thenReturn(updatedSchedules);
    
        // Perform POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/patients/bookAppointment")
                                            .sessionAttr("session_patient", p1)
                                            .param("doctorUsername", "d1")
                                            .param("date", "2025-08-29")
                                            .param("startTime", "10:00:00"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("patients/viewSchedulePage"))
            .andExpect(model().attribute("patient", p1))
            .andExpect(model().attribute("schedules", hasSize(2)))
            .andExpect(model().attribute("schedules", not(hasItem(
                allOf(
                    hasProperty("doctorUsername", is("d1")),
                    hasProperty("date", is(s1.getDate())),
                    hasProperty("startTime", is(s1.getStartTime()))
                )
            )))) // s1 should not be in schedules anymore
            .andExpect(model().attribute("schedules", hasItem(
                allOf(
                    hasProperty("doctorUsername", is(oldApt.getDoctorUsername())),
                    hasProperty("date", is(oldApt.getDate())),
                    hasProperty("startTime", is(oldApt.getStartTime()))
                )
            ))) // oldApt should be in schedules
            .andExpect(model().attribute("appointment", allOf(
                hasProperty("doctorUsername", is("d1")),
                hasProperty("date", is(s1.getDate())),
                hasProperty("startTime", is(s1.getStartTime()))
            )));
    
        // Verify interactions
        verify(scheduleRepo, times(1)).findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"));
        verify(scheduleRepo, times(1)).save(any(Schedule.class)); // Verify the updated schedule after old appointment is added
        verify(appointmentRepo, times(1)).delete(oldApt); // Verify deletion of old appointment
        verify(appointmentRepo, times(1)).save(any(Appointment.class)); // Verify saving of new appointment
        verify(scheduleRepo, times(1)).delete(s1); // Verify deletion of booked schedule
        verify(scheduleRepo, times(1)).findAll(); // Verify fetching all schedules twice (before and after booking)
        verify(appointmentRepo, times(1)).findByPatientUsername("p1"); // Verify findByPatientUsername was called twice
    }
    
    // 2B. test patient book appointment - POST - Case 2:  patient don't have appointment 
    @Test
    public void testValidBookAppointment2() throws Exception {
        // Setup patient and schedule data
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);

        // Mock repository methods
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"))).thenReturn(s1);

        // Mock old appointment case
        Appointment oldApt = null;
        when(appointmentRepo.findByPatientUsername("p1")).thenReturn(oldApt);
        
        List<Schedule> updateSchedule = new ArrayList<>();
        updateSchedule.add(s2);
        when(scheduleRepo.findAll()).thenReturn(updateSchedule);
 
        // Perform POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/patients/bookAppointment")
                .sessionAttr("session_patient", p1)
                .param("doctorUsername", "d1")
                .param("date", "2025-08-29")
                .param("startTime", "10:00:00"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("patients/viewSchedulePage"))
            .andExpect(model().attribute("patient", p1))

            .andExpect(model().attribute("schedules", hasSize(1)))
            .andExpect(model().attribute("schedules", not(hasItem(
                allOf(
                    hasProperty("doctorUsername", is("d1")),
                    hasProperty("date", is(s1.getDate())),
                    hasProperty("startTime", is(s1.getStartTime()))
                )
            ))))
            .andExpect(model().attribute("appointment", allOf(
                hasProperty("doctorUsername", is("d1")),
                hasProperty("date", is(s1.getDate())),
                hasProperty("startTime", is(s1.getStartTime()))
            )));

        // Verify interactions
        verify(scheduleRepo, times(1)).findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"));
        verify(appointmentRepo, times(1)).save(any(Appointment.class));
        verify(scheduleRepo, times(0)).save(any(Schedule.class));
        verify(appointmentRepo, times(0)).delete(oldApt);
        verify(scheduleRepo, times(1)).delete(s1);
        verify(scheduleRepo, times(1)).findAll();
}


    // 3B. test patient delete appointment - POST - Case 1: success
    @Test
    public void testValidDeleteAppointment() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        
        // Mock schedule before delete
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);
        
        Appointment oldApt = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        when(appointmentRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-06-30"), Time.valueOf(("09:00:00")))).thenReturn(oldApt);

        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        schedules.add(newSche);
        when(scheduleRepo.findAll()).thenReturn(schedules);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/deleteAppointment")
                                            .sessionAttr("session_patient", p1)
                                            .param("doctorUsername", "d1")
                                            .param("date", "2025-06-30")
                                            .param("startTime", "09:00:00")
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/viewSchedulePage"))
                // test whether change appointment to past appointment
                .andExpect(model().attribute("schedules", hasSize(3)))    
                .andExpect(model().attribute("schedules", hasItem(allOf(
                    hasProperty("doctorUsername", is("d1")),
                    hasProperty("date", is(Date.valueOf("2025-06-30"))),
                    hasProperty("startTime", is(Time.valueOf("09:00:00")))                                 
                ))))    
        ;     
        
        // verify list of steps
        verify(appointmentRepo, times(1)).findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"));
        verify(scheduleRepo, times(1)).save(any(Schedule.class));
        verify(appointmentRepo, times(1)).delete(any(Appointment.class));
        verify(scheduleRepo, times(1)).findAll();
    }

     // 3A. test patient delete appointment - POST - Case 2: patient not login
    @Test
    public void testInvalidDeleteAppointment() throws Exception {
        
        // Mock schedule before delete
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);
        
        Appointment oldApt = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        when(appointmentRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-06-30"), Time.valueOf(("09:00:00")))).thenReturn(oldApt);

        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        schedules.add(newSche);
        when(scheduleRepo.findAll()).thenReturn(schedules);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/deleteAppointment")
                                            .param("doctorUsername", "d1")
                                            .param("date", "2025-06-30")
                                            .param("startTime", "09:00:00")
                        )
                .andExpect(MockMvcResultMatchers.view().name("loginPage"))
                .andExpect(model().attributeDoesNotExist("schedules"))
        ;     
        
        // verify list of steps
        verify(appointmentRepo, times(0)).findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"));
        verify(scheduleRepo, times(0)).save(any(Schedule.class));
        verify(appointmentRepo, times(0)).delete(any(Appointment.class));
        verify(scheduleRepo, times(0)).findAll();
    }

    // 4A. test patient view past apt - GET - SUCCESS
    @Test
    public void testValidViewPastApt() throws Exception {
        Patient p1 = new Patient("p1", "123456", "patient1", 20, "123St", "123");
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Time.valueOf("13:15:00"), 10, Department.valueOf("General"));
        List<PastAppointment> pastAptList = new ArrayList<>();
        pastAptList.add(pastApt);

        List<PastAppointment> nonFeedbackPastAptList = new ArrayList<>();
        nonFeedbackPastAptList.add(pastApt);
        when(pastAppointmentRepo.findByPatientUsername(p1.getUsername())).thenReturn(pastAptList);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewPastAppointment")
                                            .sessionAttr("session_patient", p1)
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/viewPastAptPage"))
                .andExpect(model().attribute("patient", p1))
                .andExpect(model().attribute("nonFeedbackList", nonFeedbackPastAptList));  
    }

    // 4B. test patient view past apt - GET - INVALID (patient not log in)
    @Test
    public void testInvalidViewPastApt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewPastAppointment"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("loginPage"))
                .andExpect(model().attributeDoesNotExist("patient"))
                .andExpect(model().attributeDoesNotExist("nonFeedbackList"));  

        verify(pastAppointmentRepo, times(0)).findByPatientUsername("p1");
    }

    //------------------------------------Test Record--------------------------------------
    // 1A. test patient view record - GET - case 1: success
    @Test 
    public void testValidGetRecord() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Record r1 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-06-27"));
        Record r2 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-05-17"));
        Record r3 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-04-07"));

        List<Record> records = new ArrayList<>();
        records.add(r1);
        records.add(r2);
        records.add(r3);

        when(recordRepo.findByPatientUsername(p1.getUsername())).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewRecord")
                                            .sessionAttr("session_patient", p1)
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/viewRecordPage"))
                .andExpect(model().attribute("records", hasSize(3)))            
        ;
    }

    // 1B. test patient view record - GET - case 2: patient not log in
    @Test 
    public void testInvalidGetRecord() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Record r1 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-06-27"));
        Record r2 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-05-17"));
        Record r3 = new Record("p1", "patient1", "d1", "doctor1", Department.valueOf("General"), "You are good", Date.valueOf("2024-04-07"));

        List<Record> records = Arrays.asList(r1, r2, r3);

        when(recordRepo.findByPatientUsername(p1.getUsername())).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewRecord"))   
                .andExpect(MockMvcResultMatchers.view().name("loginPage"))
                .andExpect(model().attributeDoesNotExist("records"))           
        ;
    }

    //------------------------------------Test Event------------------------------------
  
    // 1A. test patient view event - GET
    // @Test
    // public void testValidGetEvent() throws Exception {
    //     Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
    //     Event e1 = new Event("E101", "Event 1", 150, "Welcome to event 1", Date.valueOf("2024-09-27"), Time.valueOf("15:00:00"), 90);
    //     Event e2 = new Event("E102", "Event 2", 100, "Welcome to event 2", Date.valueOf("2024-10-12"), Time.valueOf("12:00:00"), 120);
    //     Event e3 = new Event("E103", "Event 3", 200, "Welcome to event 3", Date.valueOf("2024-11-30"), Time.valueOf("9:00:00"), 60);

    //     List<Event> bookedEvents = new ArrayList<>();
    //     List<Event> unbookedEvents = new ArrayList<>();

    //     p1.getEventsJoin().add(e1.getEventCode()); // e1 is a booked event

    //     bookedEvents.add(e1);
    //     unbookedEvents.add(e2);
    //     unbookedEvents.add(e3);

    //     mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewEvent")
    //                                         .sessionAttr("session_patient", p1)
    //                     )
    //             .andExpect(MockMvcResultMatchers.status().isOk())
    //             .andExpect(MockMvcResultMatchers.view().name("patients/viewEventPage"))
    //             .andExpect(model().attribute("booked", hasSize(1)))
    //             .andExpect(model().attribute("unbooked", hasSize(2)))
    //     ;
    // }


    //------------------------------------Test Feedback------------------------------------

    // 1A. test patient view feedback - GET - Case 1: SUCCESS
    @Test
    public void testValidViewFeedback() throws Exception {
    Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
    Doctor d1 = new Doctor("d1", "123456", "doctor1", 20, "123", "123", Department.Dermatology);
    Feedback f1 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2024-05-05"), Department.valueOf("General"), "Good doctor");
    f1.setRate(4);
    Feedback f2 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2024-06-06"), Department.valueOf("General"), "Good doctor");
    f2.setRate(5);

    List<Feedback> feedbackList = new ArrayList<>();
    feedbackList.add(f1);
    feedbackList.add(f2);

    when(doctorRepo.findByUsername(d1.getUsername())).thenReturn(d1);  
    when(feedbackRepo.findByDoctorUsername(d1.getUsername())).thenReturn(feedbackList);  

    mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewFeedback")
            .param("doctorUsername", d1.getUsername())
            .sessionAttr("session_patient", p1))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("patients/viewFeedbackPage"))
            .andExpect(model().attribute("doctor", d1))
            .andExpect(model().attribute("feedbackList", hasSize(2)))
            .andExpect(model().attribute("feedbackList", hasItem(allOf(
                hasProperty("doctorUsername", is("d1")),
                hasProperty("date", is(f1.getDate())),
                hasProperty("rate", is(f1.getRate()))
            ))));
    
    // Removed unnecessary verification
    verify(doctorRepo, times(1)).findByUsername(d1.getUsername());
    verify(feedbackRepo, times(1)).findByDoctorUsername(d1.getUsername());
}


    // 1B. test patient view feedback - GET - Case 2: patient not log in
    @Test 
    public void testInvalidViewFeedback() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewFeedback")
                .param("doctorUsername", "someDoctorUsername"))     // if it requests param => must include eventho still not get any data from that
                // must write for all parameters 
                .andExpect(MockMvcResultMatchers.view().name("loginPage"))
                .andExpect(model().attributeDoesNotExist("feedbackList"))
                .andExpect(model().attributeDoesNotExist("doctor"));
        
        // verify statements => b/c redirect to login after reach the repo call => 0 times
        verify(doctorRepo, times(0)).findByUsername("d1");
        verify(feedbackRepo, times(0)).findByDoctorUsername("d1");
    }

    // 2A. test patient add feedback - GET - Case 1: SUCCESS (patient log in)
    @Test
    public void testValidGetFeedback() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Time.valueOf("13:15:00"), 10, Department.valueOf("General"));
        when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"))).thenReturn(pastApt);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/addFeedback")
            .sessionAttr("session_patient", p1)
            .param("patientUsername", "p1")
            .param("doctorUsername", "d1")
            .param("date", "2023-05-08"))    // NOTE: PASS DATE TIME VALUE AS STRING
            
            .andExpect(MockMvcResultMatchers.view().name("patients/addFeedbackPage"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(model().attribute("nonFeedbackPastApt", pastApt));

        // verify statements can be used to ensure the repository method was called
        verify(pastAppointmentRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"));
    }

    // 2B. test patient add feedback - GET - Case 2: patient not log in
    @Test
    public void testInvalidGetFeedback() throws Exception {
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Time.valueOf("13:15:00"), 10, Department.valueOf("General"));
        when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"))).thenReturn(pastApt);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/addFeedback")
            // .sessionAttr("session_patient", p1)      // if test invalid session => remove this
            .param("patientUsername", "p1")
            .param("doctorUsername", "d1")
            .param("date", "2023-05-08"))    // NOTE: PASS DATE TIME VALUE AS STRING
            
            .andExpect(MockMvcResultMatchers.view().name("loginPage"))
            .andExpect(model().attributeDoesNotExist("nonFeedbackPastApt"));

        // verify statements can be used to ensure the repository method was called
        verify(pastAppointmentRepo, times(0)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"));
    }

    // 3A. test patient add feedback - POST - Case 1: SUCCESS (ie description not empty && rate not empty)
    @Test
public void testValidAddFeedback() throws Exception {
    Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
    PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Time.valueOf("13:15:00"), 10, Department.valueOf("General"));
    Feedback f1 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Department.valueOf("General"), "Good doctor");
    f1.setRate(4);
   
    Doctor doc = new Doctor("d1", "123", "doctor1", 22, "123", "123", Department.General);
   
    List<Feedback> feedbackList = new ArrayList<>();
    feedbackList.add(f1);

    // Mocking necessary repository calls
    when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"))).thenReturn(pastApt);
    when(doctorRepo.findByUsername("d1")).thenReturn(doc);
    when(feedbackRepo.findByPatientUsername("p1")).thenReturn(feedbackList);

    mockMvc.perform(MockMvcRequestBuilders.post("/patients/addFeedback")
        .sessionAttr("session_patient", p1)
        .param("doctorUsername", "d1")
        .param("patientUsername", "p1")
        .param("date", "2023-05-08")
        .param("feedbackStr", "Good doctor")
        .param("rate", "4"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("patients/viewPastAptPage"))
        .andExpect(model().attributeDoesNotExist("error0"))
        .andExpect(model().attributeDoesNotExist("nonFeedbackPastApt"))
        .andExpect(model().attribute("doctor", doc));

    // Verify repository interactions
    verify(pastAppointmentRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"));
    verify(doctorRepo, times(1)).findByUsername("d1");
    verify(feedbackRepo, times(1)).save(any(Feedback.class));
    verify(feedbackRepo, times(1)).findByDoctorUsername("d1");
    verify(pastAppointmentRepo, times(1)).save(any(PastAppointment.class));
}


    // 3B. test patient add feedback - POST - Case 2: empty description 
    @Test
    public void testInvalidAddFeedback() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Time.valueOf("13:15:00"), 10, Department.valueOf("General"));
        Feedback f1 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Department.valueOf("General"), "Good doctor");
        Feedback f2 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-15"), Department.valueOf("General"), "Nice doctor");
       
        Doctor doc = new Doctor("d1", "123", "doctor1", 22, "123", "123", Department.General);
       
        List<Feedback> feedbackList = new ArrayList<>();
        feedbackList.add(f1);
        feedbackList.add(f2);

        // Mocking necessary repository calls
        when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"))).thenReturn(pastApt);
        when(doctorRepo.findByUsername("d1")).thenReturn(doc);
        when(feedbackRepo.findByPatientUsername("p1")).thenReturn(feedbackList);

        // param is unaffected by feedback obj above => based on user's input
        mockMvc.perform(MockMvcRequestBuilders.post("/patients/addFeedback")        // change to POST in PostMapping         
            .sessionAttr("session_patient", p1)
            .param("doctorUsername", "d1")
            .param("patientUsername", "p1")
            .param("date", "2023-05-08")
            .param("feedbackStr", ""))       // empty field

            .andExpect(MockMvcResultMatchers.status().is(400))
            .andExpect(MockMvcResultMatchers.view().name("patients/addFeedbackPage"))
            .andExpect(model().attributeExists("error0"))
            .andExpect(model().attributeExists("nonFeedbackPastApt"))
            .andExpect(model().attributeDoesNotExist("feedbackList"))
            .andExpect(model().attributeDoesNotExist("nonFeedbackList"));

        // verify statements
        verify(pastAppointmentRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"));
        verify(doctorRepo, times(0)).findByUsername("d1");
        verify(feedbackRepo, times(0)).save(any(Feedback.class));
        verify(feedbackRepo, times(0)).findByPatientUsername("p1");
        verify(pastAppointmentRepo, times(0)).save(any(PastAppointment.class));
        verify(pastAppointmentRepo, times(0)).findByPatientUsername("p1");
    }

    // 3C. test patient add feedback - POST - case 3: empty rating
    @Test
    public void testAddFeedbackEmptyRate() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        PastAppointment pastApt = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Time.valueOf("13:15:00"), 10, Department.valueOf("General"));
        Feedback f1 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-08"), Department.valueOf("General"), "Good doctor");
        Feedback f2 = new Feedback("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-05-15"), Department.valueOf("General"), "Nice doctor");
       
        Doctor doc = new Doctor("d1", "123", "doctor1", 22, "123", "123", Department.General);
       
        List<Feedback> feedbackList = new ArrayList<>();
        feedbackList.add(f1);
        feedbackList.add(f2);

        // Mocking necessary repository calls
        when(pastAppointmentRepo.findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"))).thenReturn(pastApt);
        when(doctorRepo.findByUsername("d1")).thenReturn(doc);
        when(feedbackRepo.findByPatientUsername("p1")).thenReturn(feedbackList);

        // param is unaffected by feedback obj above => based on user's input
        mockMvc.perform(MockMvcRequestBuilders.post("/patients/addFeedback")        // change to POST in PostMapping         
            .sessionAttr("session_patient", p1)
            .param("doctorUsername", "d1")
            .param("patientUsername", "p1")
            .param("date", "2023-05-08")
            .param("feedbackStr", "Good")
            .param("rate", ""))    // empty field     
            

            .andExpect(MockMvcResultMatchers.status().is(400))
            .andExpect(MockMvcResultMatchers.view().name("patients/addFeedbackPage"))
            .andExpect(model().attributeExists("error0"))
            .andExpect(model().attributeExists("nonFeedbackPastApt"))
            .andExpect(model().attributeDoesNotExist("feedbackList"))
            .andExpect(model().attributeDoesNotExist("nonFeedbackList"));

        // verify statements
        verify(pastAppointmentRepo, times(1)).findByPatientUsernameAndDoctorUsernameAndDate("p1", "d1", Date.valueOf("2023-05-08"));
        verify(doctorRepo, times(0)).findByUsername("d1");
        verify(feedbackRepo, times(0)).save(any(Feedback.class));
        verify(feedbackRepo, times(0)).findByPatientUsername("p1");
        verify(pastAppointmentRepo, times(0)).save(any(PastAppointment.class));
        verify(pastAppointmentRepo, times(0)).findByPatientUsername("p1");
    }


    // ----------------------------------------------------Edit information----------------------------------------------------------------
    
    // 1A. Test valid edit - Case 1: Success
    @Test
    public void testValidEdit() throws Exception {
        Patient p1 = new Patient("p1", "123456", "TBD", 20, "TBD", "TBD");

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/editInformation")
                                            .sessionAttr("session_patient", p1)
                                            .param("name", "patient1")
                                            .param("age", "30")
                                            .param("address", "123")
                                            .param("phone", "123"))
            .andExpect(MockMvcResultMatchers.status().is(200))
            .andExpect(MockMvcResultMatchers.view().name("patients/editInformationPage"))
            .andExpect(model().attributeExists("patient"))
            .andExpect(model().attributeDoesNotExist("error0"))
            .andExpect(model().attributeDoesNotExist("error1"))
            .andExpect(model().attribute("patient", allOf(
                hasProperty("name",is("patient1")),
                hasProperty("age",is(30)),
                hasProperty("address",is("123")),
                hasProperty("phone",is("123"))
            )))    
            ;

        verify(patientRepo, times(1)).save(any(Patient.class));
    }

    // 1B. Test valid edit - Case 2: unsuccessfully - lack field
    @Test
    public void testInvalidEdit1() throws Exception {
        Patient p1 = new Patient("p1", "123456", "TBD", 20, "TBD", "TBD");

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/editInformation")
                                            .sessionAttr("session_patient", p1)
                                            .param("name", "")
                                            .param("age", "30")
                                            .param("address", "123")
                                            .param("phone", "123"))
            .andExpect(MockMvcResultMatchers.status().is(400))
            .andExpect(MockMvcResultMatchers.view().name("patients/editInformationPage"))
            .andExpect(model().attributeExists("error0"))
            .andExpect(model().attributeDoesNotExist("success"))
            .andExpect(model().attributeDoesNotExist("error1"))
            .andExpect(model().attribute("patient", allOf(
                hasProperty("name",is("TBD")),
                hasProperty("age",is(20)),
                hasProperty("address",is("TBD")),
                hasProperty("phone",is("TBD"))
            )))    
            ;

        verify(patientRepo, times(0)).save(any(Patient.class));
    }

    
    // 1B. Test valid edit - Case 3: unsuccessfully - invalid age
    @Test
    public void testInvalidEdit2() throws Exception {
        Patient p1 = new Patient("p1", "123456", "TBD", 20, "TBD", "TBD");

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/editInformation")
                                            .sessionAttr("session_patient", p1)
                                            .param("name", "patient1")
                                            .param("age", "-30")
                                            .param("address", "123")
                                            .param("phone", "123"))
            .andExpect(MockMvcResultMatchers.status().is(400))
            .andExpect(MockMvcResultMatchers.view().name("patients/editInformationPage"))
            .andExpect(model().attributeExists("error1"))
            .andExpect(model().attributeDoesNotExist("success"))
            .andExpect(model().attributeDoesNotExist("error0"))
            .andExpect(model().attribute("patient", allOf(
                hasProperty("name",is("TBD")),
                hasProperty("age",is(20)),
                hasProperty("address",is("TBD")),
                hasProperty("phone",is("TBD"))
            )))    
            ;

        verify(patientRepo, times(0)).save(any(Patient.class));
    }

    // ----------------------------------------------------Test ratings----------------------------------------------------------------
    // 1A. test patient view rating - GET - success
    @Test
    public void testValidViewRating() throws Exception {
        Patient p1 = new Patient("p1", "123456", "patient1", 20, "123St", "123");

        Doctor d1 = new Doctor("d1", "123456", "John Doe", 20, "123", "123", Department.Dermatology);
        Doctor d2 = new Doctor("d2", "123456", "Jane Smith", 25, "123", "123", Department.Cardiology);

        List<Doctor> doctors = Arrays.asList(d1, d2);
        when(doctorRepo.findAll()).thenReturn(doctors);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewRating")
                .sessionAttr("session_patient", p1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/viewRatingPage"))
                .andExpect(model().attribute("doctors", hasSize(2)))
                .andExpect(model().attribute("doctors", hasItem(allOf(
                    hasProperty("name", is("John Doe")),
                    hasProperty("username", is("d1"))
                ))))
                .andExpect(model().attribute("doctors", hasItem(allOf(
                    hasProperty("name", is("Jane Smith")),
                    hasProperty("username", is("d2"))
                ))));

        // Verify that the findAll method was called once
        verify(doctorRepo, times(1)).findAll();
    }
    
    // 1B. test patient view rating - GET - invalid (ie patient not log in)
    @Test
    public void testInvalidViewRating() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/viewRating"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("loginPage"))
                .andExpect(model().attributeDoesNotExist("doctors"));

        // Verify that no repository methods are called since the patient is not logged in
        verify(doctorRepo, times(0)).findAll();
    }
}




    

    



