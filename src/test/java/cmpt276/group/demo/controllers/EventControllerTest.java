package cmpt276.group.demo.controllers;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cmpt276.group.demo.DemoApplication;
import cmpt276.group.demo.api.GMailer;
import cmpt276.group.demo.models.event.Event;
import cmpt276.group.demo.models.event.EventRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;


@WebMvcTest(EventController.class)
@ContextConfiguration(classes = DemoApplication.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private EventRepository eventRepo;

    @MockBean
    private GMailer gMailer;

    // Test patient book an event
    // Case 1: patient can book event 
    @Test
    public void testValidBookEvent() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        
        Event e1 = new Event("E101", "Event 101", 10, "This is new event", Date.valueOf("2024-12-08"), Time.valueOf("10:00:00"), 90);
        Event e2 = new Event("E102", "Event 102", 10, "This is new event", Date.valueOf("2024-12-09"), Time.valueOf("10:00:00"), 90);
        Event e3 = new Event("E103", "Event 103", 10, "This is new event", Date.valueOf("2024-12-10"), Time.valueOf("10:00:00"), 90);
    
        List<Event> events = new ArrayList<>();
        events.add(e1);
        events.add(e2);
        events.add(e3);
        
        when(eventRepo.findAll()).thenReturn(events);
        when(eventRepo.findByEventCode("E101")).thenReturn(e1);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/addEvent")
                                            .sessionAttr("session_patient", p1)
                                            .param("eventCode", "E101"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/viewEventPage"))
                .andExpect(MockMvcResultMatchers.model().attribute("booked", hasSize(1)))
                .andExpect(MockMvcResultMatchers.model().attribute("unbooked", hasSize(2)))
                .andExpect(MockMvcResultMatchers.model().attribute("booked", hasItem(allOf(
                    hasProperty("eventCode", is("E101"))
                ))))
                .andExpect(MockMvcResultMatchers.model().attribute("unbooked", hasItem(allOf(
                    hasProperty("eventCode", is("E102"))
                ))));
    }

    // case 2: patient can't book (event full)
    @Test
    public void testInvalidBookEvent() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        
        Event e1 = new Event("E101", "Event 101", 10, "This is new event", Date.valueOf("2024-12-08"), Time.valueOf("10:00:00"), 90);
        Event e2 = new Event("E102", "Event 102", 10, "This is new event", Date.valueOf("2024-12-09"), Time.valueOf("10:00:00"), 90);
        Event e3 = new Event("E103", "Event 103", 10, "This is new event", Date.valueOf("2024-12-10"), Time.valueOf("10:00:00"), 90);
        e1.setCurrentNum(10);

        List<Event> events = new ArrayList<>();
        events.add(e1);
        events.add(e2);
        events.add(e3);
        
        when(eventRepo.findAll()).thenReturn(events);
        when(eventRepo.findByEventCode("E101")).thenReturn(e1);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/addEvent")
                                            .sessionAttr("session_patient", p1)
                                            .param("eventCode", "E101"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/viewEventPage"))
                .andExpect(MockMvcResultMatchers.model().attribute("booked", hasSize(0)))
                .andExpect(MockMvcResultMatchers.model().attribute("unbooked", hasSize(3)))
                .andExpect(MockMvcResultMatchers.model().attribute("booked", not(hasItem(allOf(
                    hasProperty("eventCode", is("E101"))
                )))))
                .andExpect(MockMvcResultMatchers.model().attribute("unbooked", hasItem(allOf(
                    hasProperty("eventCode", is("E102"))
                ))));
    }

    // Test patient book an event
    // case 1: patient cancel event successfully
    @Test
    public void testValidDeleteEvent() throws Exception {
        Patient p1 = new Patient("p1", "123", "patient1", 20, "123St", "123");
        
        Event e1 = new Event("E101", "Event 101", 10, "This is new event", Date.valueOf("2024-12-08"), Time.valueOf("10:00:00"), 90);
        Event e2 = new Event("E102", "Event 102", 10, "This is new event", Date.valueOf("2024-12-09"), Time.valueOf("10:00:00"), 90);
        Event e3 = new Event("E103", "Event 103", 10, "This is new event", Date.valueOf("2024-12-10"), Time.valueOf("10:00:00"), 90);
        
        String[] nameArray = {"Bob", "Alice", "Chi", "Don", "Eva", "p1"};
        List<String> patients = new ArrayList<>(Arrays.asList(nameArray));
        e1.setPatients(patients);

        List<Event> events = new ArrayList<>();
        events.add(e1);
        events.add(e2);
        events.add(e3);
        
        when(eventRepo.findAll()).thenReturn(events);
        when(eventRepo.findByEventCode("E101")).thenReturn(e1);

        mockMvc.perform(MockMvcRequestBuilders.post("/patients/deleteEvent")
                                            .sessionAttr("session_patient", p1)
                                            .param("eventCode", "E101"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patients/viewEventPage"))
                .andExpect(MockMvcResultMatchers.model().attribute("booked", hasSize(0)))
                .andExpect(MockMvcResultMatchers.model().attribute("unbooked", hasSize(3)))
                .andExpect(MockMvcResultMatchers.model().attribute("unbooked", hasItem(allOf(
                    hasProperty("eventCode", is("E101")),
                    hasProperty("currentNum", is(5))
                ))))
                .andExpect(MockMvcResultMatchers.model().attribute("unbooked", hasItem(allOf(
                    hasProperty("eventCode", is("E102"))
                ))));
    }


}
