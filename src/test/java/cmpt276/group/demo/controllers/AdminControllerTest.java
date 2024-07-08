package cmpt276.group.demo.controllers;

import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cmpt276.group.demo.models.Department;
import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.event.EventRepository;
import cmpt276.group.demo.models.feedback.FeedbackRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.schedule.Schedule;
import cmpt276.group.demo.models.schedule.ScheduleRepository;


@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private DoctorRepository doctorRepo;

    @MockBean
    private ScheduleRepository scheduleRepo;

    @MockBean
    private AppointmentRepository appointmentRepo;

    @MockBean
    private PastAppointmentRepository pastAppointmentRepo;

    @MockBean
    private EventRepository eventRepo;

    @MockBean
    private FeedbackRepository feedbackRepo;

    @Autowired
    private MockMvc mockMvc;

    // -------------------------------------------Test get dashboard ----------------------------------------------
     // 1A. test patient view dashboard - GET - Valid
    @Test
    public void testValidViewDashboard() throws Exception {
        Admin a1 = new Admin("admin", "123");
        
        mockMvc.perform(MockMvcRequestBuilders.get("/admins/getDashboard")
                                                .sessionAttr("session_admin", a1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/mainPage"));
    }

    // ------------------------------------------Test view & add, delete doctor------------------------------------
    
    // 1. Test admin view doctor - GET - case 1: success
    @Test
    public void testValidViewDoctor() throws Exception {
        Doctor d1 = new Doctor("Doc1", "123", "Doctor 1", 25, "123st", "888-888-8888", Department.General);
        Doctor d2 = new Doctor("Doc2", "123", "Doctor 2", 20, "123st", "888-888-8888", Department.Cardiology);
        Doctor d3 = new Doctor("Doc3", "123", "Doctor 3", 30, "123st", "888-888-8888", Department.Orthopedics);
    
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(d1);
        doctors.add(d2);
        doctors.add(d3);

        when(doctorRepo.findAll()).thenReturn(doctors);

        mockMvc.perform(MockMvcRequestBuilders.get("/admins/viewDoctor"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/viewDoctorPage"))
        ;
    }

    // 2A. Test admin add doctor - POST - case 1: success
    @Test
    public void testValidAddDoctor() throws Exception {
        Doctor d1 = new Doctor("Doc1", "password", "Doctor 1", 30, "123st", "888-888-8888", Department.General);
        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addDoctor")
                                            .param("username", "Doc1")
                                            .param("password", "password")
                                            .param("name", "Doctor 1")
                                            .param("address", "123st")
                                            .param("phone", "888-888-8888")
                                            .param("department", "General")
                                            .param("age", "30")
                        )
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.view().name("admins/addDoctorPage")) 
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("error2"))
                .andExpect(model().attributeExists("success"))
                .andExpect(model().attribute("Doctor", allOf(
                    hasProperty("username", is(d1.getUsername())),
                    hasProperty("password", is(d1.getPassword())),
                    hasProperty("name", is(d1.getName()))
                )))
                        
        ; 
    }

    // 2B. Test admin add doctor - POST - case 2: error 0 - lack field
    @Test
    public void testInvalidAddDoctor1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addDoctor")
                                            .param("username", "Doc1")
                                            .param("password", "")
                                            .param("name", "Doctor 1")
                                            .param("address", "123st")
                                            .param("phone", "888-888-8888")
                                            .param("department", "General")
                                            .param("age", "30")
                        )
                .andExpect(status().is(400)) // Mean SC_BAD_REQUEST
                .andExpect(MockMvcResultMatchers.view().name("admins/addDoctorPage")) 
                .andExpect(model().attributeExists("error0"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("error2"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("Doctor"))
        ; 
        verify(doctorRepo, times(0)).findByUsername("Doc1");
        verify(doctorRepo, times(0)).save(any(Doctor.class));
    }

    // 2C. Test admin add doctor - POST - case 3: error 1 -  age < 0
    @Test
    public void testInvalidAddDoctor2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addDoctor")
                                            .param("username", "Doc1")
                                            .param("password", "password")
                                            .param("name", "Doctor 1")
                                            .param("address", "123st")
                                            .param("phone", "888-888-8888")
                                            .param("department", "General")
                                            .param("age", "-30")
                        )
                .andExpect(status().is(400)) // Mean SC_BAD_REQUEST
                .andExpect(MockMvcResultMatchers.view().name("admins/addDoctorPage")) 
                .andExpect(model().attributeExists("error1"))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error2"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("Doctor"))
        ; 
        verify(doctorRepo, times(0)).findByUsername("Doc1");
        verify(doctorRepo, times(0)).save(any(Doctor.class));
    }

    // 2D. Test admin add doctor - POST - case 4: error 2 -  doctor username exist
    @Test
    public void testInvalidAddDoctor3() throws Exception {
        Doctor d1 = new Doctor("Doc1", "123", "Exist doc", 40, "123st", "888-123-1234", Department.Ophthalmology);

        when(doctorRepo.findByUsername("Doc1")).thenReturn(d1);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addDoctor")
                                            .param("username", "Doc1")
                                            .param("password", "password")
                                            .param("name", "Doctor 1")
                                            .param("address", "123st")
                                            .param("phone", "888-888-8888")
                                            .param("department", "General")
                                            .param("age", "30")
                        )
                .andExpect(status().is(400)) // Mean SC_BAD_REQUEST
                .andExpect(MockMvcResultMatchers.view().name("admins/addDoctorPage")) 
                .andExpect(model().attributeExists("error2"))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("Doctor"))
        ; 

        verify(doctorRepo, times(1)).findByUsername("Doc1");
        verify(doctorRepo, times(0)).save(any(Doctor.class));
    }

    // 3. Test admin delete doctor - POST - case 1 : all schedule and appointment associate will be delete
    @Test
    public void testValidDeleteDoctor() throws Exception {
        Doctor d1 = new Doctor("d1", "123", "doctor 1", 40, "123st", "888-123-1234", Department.Ophthalmology);
        Doctor d2 = new Doctor("d2", "123", "doctor 2", 40, "123st", "888-123-1234", Department.Ophthalmology);
        Doctor d3 = new Doctor("d3", "123", "doctor 3", 40, "123st", "888-123-1234", Department.Ophthalmology);

        Schedule s1 = new Schedule("doctor 1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.valueOf("Ophthalmology"));
        Schedule s2 = new Schedule("doctor 1", "d1", Date.valueOf("2025-08-28"), Time.valueOf("10:00:00"), 10, Department.valueOf("Ophthalmology"));
        Appointment a1 = new Appointment("doctor 1", "d1", "patient1", "p1", Date.valueOf("2024-10-10"), Time.valueOf("10:00:00"), 10, Department.valueOf("Ophthalmology"));
        
        List<Schedule> schedules = new ArrayList<>();
        List<Appointment> appointments = new ArrayList<>();
        List<Doctor> doctors = new ArrayList<>();

        schedules.add(s1);
        schedules.add(s2);
        appointments.add(a1);
    
        doctors.add(d2);
        doctors.add(d3);

        when(appointmentRepo.findByDoctorUsername("d1")).thenReturn(appointments);
        when(scheduleRepo.findByDoctorUsername("d1")).thenReturn(schedules);
        when(doctorRepo.findByUsername("d1")).thenReturn(d1);
        when(doctorRepo.findAll()).thenReturn(doctors);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/deleteDoctor")
                                            .param("username","d1")                        
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/viewDoctorPage"))
                .andExpect(model().attribute("doctors", doctors))
                .andExpect(model().attribute("doctors", hasSize(2)))
                .andExpect(model().attribute("doctors", not(hasItem(allOf(
                        hasProperty("username", is(d1.getUsername()))
                )))))

        ;

        verify(appointmentRepo, times(1)).delete(any(Appointment.class));
        verify(scheduleRepo, times(2)).delete(any(Schedule.class));
        verify(doctorRepo, times(1)).delete(any(Doctor.class));
    }

    // -----------------------------------------Test view patient-----------------------------------------------
    // 1. Test admin view patient - get - case 1: success
    @Test
    public void testValidViewPatient() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        Patient p2 = new Patient("p2", "123", "patient2", 30, "123St", "123");
        Patient p3 = new Patient("p3", "123", "patient1", 40, "123St", "123");
   
        List<Patient> patients = new ArrayList<>();
        patients.add(p1);
        patients.add(p2);
        patients.add(p3);

        when(patientRepo.findAll()).thenReturn(patients);

        mockMvc.perform(MockMvcRequestBuilders.get("/admins/viewPatient"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/viewPatientPage"))
                .andExpect(model().attribute("patients", patients))
        ;
    }
    
    // ------------------------------------Test view & delete appointment--------------------------------------
    // 1. Test admin view appointment - get - case 1: success
    @Test
    public void testValidViewAppointment() throws Exception {
        Appointment a1 = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        Appointment a2 = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-10-30"), Time.valueOf("08:00:00"), 30, Department.Ophthalmology);
        Appointment a3 = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-11-5"), Time.valueOf("12:30:00"), 30, Department.Orthopedics);
       
        PastAppointment pa1 = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-07-28"), Time.valueOf("09:00:00"), 30, Department.General);
        PastAppointment pa2 = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-06-13"), Time.valueOf("08:00:00"), 30, Department.Ophthalmology);
       
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(a1);
        appointments.add(a2);
        appointments.add(a3);
        List<PastAppointment> pastAppointments = new ArrayList<>();
        pastAppointments.add(pa1);
        pastAppointments.add(pa2);

        when(appointmentRepo.findAll()).thenReturn(appointments);
        when(pastAppointmentRepo.findAll()).thenReturn(pastAppointments);

        mockMvc.perform(MockMvcRequestBuilders.get("/admins/viewAppointment"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/viewAppointmentPage"))
                .andExpect(model().attribute("appointments", appointments))
                .andExpect(model().attribute("pastAppointments", pastAppointments))
        ;
    }

    // 2. Test admin delete appointment - post - case 1: success 
    @Test
    public void testValidDeleteAppointment() throws Exception {
        Appointment a1 = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-06-30"), Time.valueOf("09:00:00"), 30, Department.General);
        Appointment a2 = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-10-30"), Time.valueOf("08:00:00"), 30, Department.Ophthalmology);
        Appointment a3 = new Appointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2025-11-5"), Time.valueOf("12:30:00"), 30, Department.Orthopedics);
       
        PastAppointment pa1 = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-07-28"), Time.valueOf("09:00:00"), 30, Department.General);
        PastAppointment pa2 = new PastAppointment("doctor1", "d1", "patient1", "p1", Date.valueOf("2023-06-13"), Time.valueOf("08:00:00"), 30, Department.Ophthalmology);
       
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(a2);
        appointments.add(a3);
        List<PastAppointment> pastAppointments = new ArrayList<>();
        pastAppointments.add(pa1);
        pastAppointments.add(pa2);

        when(appointmentRepo.findAll()).thenReturn(appointments);
        when(pastAppointmentRepo.findAll()).thenReturn(pastAppointments);
        when(appointmentRepo.findByDoctorUsernameAndDateAndStartTime(a1.getDoctorUsername(), a1.getDate(), a1.getStartTime())).thenReturn(a1);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/deleteAppointment")
                                                .param("doctorUsername", "d1")
                                                .param("date", "2025-06-30")
                                                .param("startTime", "09:00:00")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/viewAppointmentPage"))
                .andExpect(model().attribute("appointments", hasSize(2)))
                .andExpect(model().attribute("pastAppointments", pastAppointments))
        ;

        // check whether create a new schedule, delete 1 appointment
        verify(scheduleRepo, times(1)).save(any(Schedule.class));
        verify(appointmentRepo, times(1)).delete(any(Appointment.class));
    }

    // ------------------------------------Test view, add & delete schedule-----------------------------------------
    // 1.  Test admin view schedule
    @Test
    public void testValidViewSchedule() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        Schedule s2 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        schedules.add(s2);
        when(scheduleRepo.findAll()).thenReturn(schedules);
      
        mockMvc.perform(MockMvcRequestBuilders.get("/admins/viewSchedule"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/viewSchedulePage"))
                .andExpect(model().attribute("schedules", schedules))
                .andExpect(model().attribute("schedules", hasSize(2)))
        ;
    }

    // 2A. Test admin add schedule - post - case 1 : success
    @Test
    public void testValidAddSchedule() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        Doctor d1 = new Doctor("d1", "123", "doctor 1", 40, "123st", "888-123-1234", Department.Ophthalmology);
    
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(d1);
        List<Schedule> sameDateSchedules = new ArrayList<>();
    
        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(doctorRepo.findAll()).thenReturn(doctors);
        when(doctorRepo.findByUsername("d1")).thenReturn(d1);
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"))).thenReturn(null);
        when(scheduleRepo.findByDoctorUsernameAndDate("d1", Date.valueOf("2025-08-29"))).thenReturn(sameDateSchedules);


        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
    
        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addSchedule")
                                            .param("doctorUsername", "d1")
                                            .param("duration", "10")
                                            .param("date", "2025-08-29")
                                            .param("startTime", "10:00")
                        )
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.view().name("admins/addSchedulePage"))
                .andExpect(MockMvcResultMatchers.model().attribute("schedule", 
                        allOf(
                            hasProperty("doctorUsername", is(newSche.getDoctorUsername())),
                            hasProperty("date", is(newSche.getDate())),
                            hasProperty("startTime", is(newSche.getStartTime()))
                        )
                ))
                .andExpect(model().attributeExists("success"))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("error2"))
                .andExpect(model().attributeDoesNotExist("error3")) 
                .andExpect(model().attributeDoesNotExist("error4"))
        ;
        verify(scheduleRepo, times(1)).save(any(Schedule.class));
    }

    // 2B. Test admin add schedule - post - case 2 : error0 - lack field
    @Test
    public void testInvalidAddSchedule1() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        Doctor d1 = new Doctor("d1", "123", "doctor 1", 40, "123st", "888-123-1234", Department.Ophthalmology);
    
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(d1);
        List<Schedule> sameDateSchedules = new ArrayList<>();

    
        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(doctorRepo.findAll()).thenReturn(doctors);
        when(doctorRepo.findByUsername("d1")).thenReturn(d1);
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"))).thenReturn(null);
        when(scheduleRepo.findByDoctorUsernameAndDate("d1", Date.valueOf("2025-08-29"))).thenReturn(sameDateSchedules);


        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
    
        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addSchedule")
                                            .param("doctorUsername", "d1")
                                            .param("duration", "10")
                                            .param("date", "")
                                            .param("startTime", "10:00")
                        )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.view().name("admins/addSchedulePage"))
                .andExpect(model().attributeExists("error0"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("schedule"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("error2"))
                .andExpect(model().attributeDoesNotExist("error3")) 
                .andExpect(model().attributeDoesNotExist("error4"))
        ;
        verify(scheduleRepo, times(0)).save(any(Schedule.class));
    }

    // 2C. Test admin add schedule - post - case 3 : error1 - duration < 0
    @Test
    public void testInvalidAddSchedule2() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        Doctor d1 = new Doctor("d1", "123", "doctor 1", 40, "123st", "888-123-1234", Department.Ophthalmology);
    
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(d1);
        List<Schedule> sameDateSchedules = new ArrayList<>();
    
        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(doctorRepo.findAll()).thenReturn(doctors);
        when(doctorRepo.findByUsername("d1")).thenReturn(d1);
        when(scheduleRepo.findByDoctorUsernameAndDate("d1", Date.valueOf("2025-08-29"))).thenReturn(sameDateSchedules);

    
        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
    
        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addSchedule")
                                            .param("doctorUsername", "d1")
                                            .param("duration", "-10")
                                            .param("date", "2025-08-29")
                                            .param("startTime", "10:00")
                        )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.view().name("admins/addSchedulePage"))
                .andExpect(model().attributeExists("error1"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("schedule"))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error2"))
                .andExpect(model().attributeDoesNotExist("error3")) 
                .andExpect(model().attributeDoesNotExist("error4"))
        ;
        verify(scheduleRepo, times(0)).save(any(Schedule.class));
    }

    // 2D. Test admin add schedule - post - case 4 : error2 - doctor doesn't exist 
    @Test
    public void testInvalidAddSchedule3() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        Doctor d1 = new Doctor("d1", "123", "doctor 1", 40, "123st", "888-123-1234", Department.Ophthalmology);
    
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(d1);
        List<Schedule> sameDateSchedules = new ArrayList<>();
    
        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(doctorRepo.findAll()).thenReturn(doctors);
        when(doctorRepo.findByUsername("d1")).thenReturn(d1);
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"))).thenReturn(null);
        when(scheduleRepo.findByDoctorUsernameAndDate("d1", Date.valueOf("2025-08-29"))).thenReturn(sameDateSchedules);

        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
    
        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addSchedule")
                                            .param("doctorUsername", "d0")
                                            .param("duration", "10")
                                            .param("date", "2025-08-29")
                                            .param("startTime", "10:00")
                        )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.view().name("admins/addSchedulePage"))
                .andExpect(model().attributeExists("error2"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("schedule"))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("error3")) 
                .andExpect(model().attributeDoesNotExist("error4"))
        ;
        verify(scheduleRepo, times(0)).save(any(Schedule.class));
    }


    // 2E. Test admin add schedule - post - case 5 : error3 - schedule already exist
    @Test
    public void testInvalidAddSchedule4() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        Doctor d1 = new Doctor("d1", "123", "doctor 1", 40, "123st", "888-123-1234", Department.Ophthalmology);
    
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(d1);
        List<Schedule> sameDateSchedules = new ArrayList<>();
    
        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        schedules.add(newSche);

        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(doctorRepo.findAll()).thenReturn(doctors);
        when(doctorRepo.findByUsername("d1")).thenReturn(d1);
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"))).thenReturn(newSche);
        when(scheduleRepo.findByDoctorUsernameAndDate("d1", Date.valueOf("2025-08-29"))).thenReturn(sameDateSchedules);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addSchedule")
                                            .param("doctorUsername", "d1")
                                            .param("duration", "10")
                                            .param("date", "2025-08-29")
                                            .param("startTime", "10:00")
                        )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.view().name("admins/addSchedulePage"))
                .andExpect(model().attributeExists("error3"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("schedule"))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("error2")) 
                .andExpect(model().attributeDoesNotExist("error4"))
        ;
        verify(scheduleRepo, times(0)).save(any(Schedule.class));
    }

    // 2F. Test admin add schedule - post - case 6 : error4 - time conflict
    @Test
    public void testInvalidAddSchedule5() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("09:55:00"), 20, Department.General);
        Doctor d1 = new Doctor("d1", "123", "doctor 1", 40, "123st", "888-123-1234", Department.Ophthalmology);
    
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(d1);
    
        Schedule newSche = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        schedules.add(newSche);

        when(scheduleRepo.findAll()).thenReturn(schedules);
        when(doctorRepo.findAll()).thenReturn(doctors);
        when(doctorRepo.findByUsername("d1")).thenReturn(d1);
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"))).thenReturn(null);
        when(scheduleRepo.findByDoctorUsernameAndDate("d1", Date.valueOf("2025-08-29"))).thenReturn(schedules);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addSchedule")
                                            .param("doctorUsername", "d1")
                                            .param("duration", "10")
                                            .param("date", "2025-08-29")
                                            .param("startTime", "10:00")
                        )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.view().name("admins/addSchedulePage"))
                .andExpect(model().attributeExists("error4"))
                .andExpect(model().attributeDoesNotExist("success"))
                .andExpect(model().attributeDoesNotExist("schedule"))
                .andExpect(model().attributeDoesNotExist("error0"))
                .andExpect(model().attributeDoesNotExist("error1"))
                .andExpect(model().attributeDoesNotExist("error2")) 
                .andExpect(model().attributeDoesNotExist("error3"))
        ;
        verify(scheduleRepo, times(0)).save(any(Schedule.class));
    }

    // 3. Test admin delete schedule
    @Test
    public void testValidDeleteSchedule() throws Exception {
        Schedule s1 = new Schedule("doctor1", "d1", Date.valueOf("2025-08-29"), Time.valueOf("10:00:00"), 10, Department.General);
        Schedule deleteSche = new Schedule("doctor1", "d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"), 10, Department.General);
        
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(s1);
        
        when(scheduleRepo.findByDoctorUsernameAndDateAndStartTime("d1", Date.valueOf("2025-07-20"), Time.valueOf("10:00:00"))).thenReturn(deleteSche);
        when(scheduleRepo.findAll()).thenReturn(schedules);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/deleteSchedule")
                                            .param("doctorUsername", "d1")
                                            .param("date", "2025-07-20")
                                            .param("startTime", "10:00:00")
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admins/viewSchedulePage"))
                .andExpect(model().attribute("schedules", schedules))
                .andExpect(model().attribute("schedules", hasSize(1)))
        ;
        verify(scheduleRepo, times(1)).delete(any(Schedule.class));
    }


}
