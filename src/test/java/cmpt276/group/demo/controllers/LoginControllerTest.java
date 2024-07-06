package cmpt276.group.demo.controllers;

import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import cmpt276.group.demo.models.admin.Admin;
import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.doctor.Doctor;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private AdminRepository adminRepo;

    @MockBean
    private DoctorRepository doctorRepo;

    @Test
    public void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }

    @Test
    public void testPatientLoginSuccess() throws Exception {
        Patient patient = new Patient();
        patient.setUsername("patient");
        patient.setPassword("passwort");

        when(patientRepo.findByUsernameAndPassword("patient", "password")).thenReturn(patient);

        mockMvc.perform(post("/")
                .param("username", "patient")
                .param("password", "password")
                .param("role", "patient"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/mainPage"))
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    public void testAdminLoginSuccess() throws Exception {
        Admin admin = new Admin("admin", "password"); 
        when(adminRepo.findByUsernameAndPassword("admin", "password")).thenReturn(admin);

        mockMvc.perform(post("/")
                .param("username", "admin")
                .param("password", "password")
                .param("role", "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admins/mainPage"))
                .andExpect(model().attributeExists("admin"));
    }

    @Test
    public void testDoctorLoginSuccess() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setUsername("doctor");
        doctor.setPassword("password");

        given(doctorRepo.findByUsernameAndPassword("doctor", "password")).willReturn(doctor);

        mockMvc.perform(post("/")
                .param("username", "doctor")
                .param("password", "password")
                .param("role", "doctor"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/mainPage"))
                .andExpect(model().attributeExists("doctor"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        given(patientRepo.findByUsernameAndPassword("invalid", "invalid")).willReturn(null);
        given(adminRepo.findByUsernameAndPassword("invalid", "invalid")).willReturn(null);
        given(doctorRepo.findByUsernameAndPassword("invalid", "invalid")).willReturn(null);

        mockMvc.perform(post("/")
                .param("username", "invalid")
                .param("password", "invalid")
                .param("role", "patient"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/users/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }
}
