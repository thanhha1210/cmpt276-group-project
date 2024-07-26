package cmpt276.group.demo.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.emergency.Emergency;
import cmpt276.group.demo.models.emergency.EmergencyRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.room.Room;
import cmpt276.group.demo.models.room.RoomRepository;

@WebMvcTest(EmergencyController.class)
public class EmergencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private DoctorRepository doctorRepo;

    @MockBean
    private AdminRepository adminRepo;

    @MockBean
    private RoomRepository roomRepo;

    @MockBean
    private EmergencyRepository emergencyRepo;

    // 1A. View emergency - success
    @Test
    public void testValidViewEmergency() throws Exception {
        Admin admin = new Admin("admin", "password");
        Emergency e1 = new Emergency("E1", "patient1", "doctor1", "room1", 6);
        Emergency e2 = new Emergency("E2", "patient2", "doctor2", "room2", 8);
        when(emergencyRepo.findAll()).thenReturn(Arrays.asList(e1, e2));

        mockMvc.perform(MockMvcRequestBuilders.get("/admins/viewEmergency")
                .sessionAttr("session_admin", admin))
            .andExpect(status().isOk())
            .andExpect(view().name("admins/viewEmergencyPage"))
            .andExpect(model().attributeExists("curEmergency"))
            .andExpect(model().attributeExists("pastEmergency"))
            .andExpect(model().attributeExists("avaiDoctors"))
            .andExpect(model().attributeExists("avaiRooms"));
    }

     //1B. View emergency - unsuccess (no login)
    @Test
    public void testInvalidViewEmergency() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admins/viewEmergency"))
            .andExpect(status().isOk())
            .andExpect(view().name("loginPage"));
    }

    //2A. Test add emergency - success 
    @Test
    public void testValidAddEmergency() throws Exception {
        Admin admin = new Admin("admin", "password");
        Room room = new Room("room1");
        when(roomRepo.findByName("room1")).thenReturn(room);
        when(patientRepo.findByUsername("patient1")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addEmergency")
                .sessionAttr("session_admin", admin)
                .param("patientUsername", "patient1")
                .param("doctorUsername", "doctor1")
                .param("room", "room1")
                .param("severity", "5"))
            .andExpect(status().is(201))
            .andExpect(view().name("admins/viewEmergencyPage"));

        verify(emergencyRepo, times(1)).save(any(Emergency.class));
        verify(roomRepo, times(1)).save(room);
    }

    //2B. Test add emergency - unsuccess (patient name is existed) 
    @Test
    public void testInvalidAddEmergency() throws Exception {
        Admin admin = new Admin("admin", "password");
        Patient p = new Patient("patient1", "123456", "patient1", 18, "123", "123");
        Room room = new Room("room1");
        when(roomRepo.findByName("room1")).thenReturn(room);
        when(patientRepo.findByUsername("patient1")).thenReturn(p);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/addEmergency")
                .sessionAttr("session_admin", admin)
                .param("patientUsername", "patient1")
                .param("doctorUsername", "doctor1")
                .param("room", "room1")
                .param("severity", "5"))
            .andExpect(status().is(400))
            .andExpect(view().name("admins/addEmergencyPage"));

        verify(emergencyRepo, times(0)).save(any(Emergency.class));
        verify(roomRepo, times(0)).save(room);
    }


    //3A. Test finish emergency - success
    @Test
    public void testFinishEmergency_withValidData() throws Exception {
        Admin admin = new Admin("admin", "password");
        Emergency emergency = new Emergency("E1", "patient1", "doctor1", "room1", 5);
    
        Room room = new Room("room1");
        room.setAvailable(false);
        when(emergencyRepo.findByEmerId("E1")).thenReturn(emergency);
        when(roomRepo.findByName("room1")).thenReturn(room);

        mockMvc.perform(MockMvcRequestBuilders.post("/admins/finishEmergency")
                .sessionAttr("session_admin", admin)
                .param("emerId", "E1"))
            .andExpect(status().isOk())
            .andExpect(view().name("admins/viewEmergencyPage"));

        verify(emergencyRepo, times(1)).save(emergency);
        verify(roomRepo, times(1)).save(room);
    }
}
