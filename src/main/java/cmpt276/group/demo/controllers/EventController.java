package cmpt276.group.demo.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.group.demo.api.GMailer;
import cmpt276.group.demo.models.event.Event;
import cmpt276.group.demo.models.event.EventRepository;
import cmpt276.group.demo.models.patient.Patient;
import cmpt276.group.demo.models.patient.PatientRepository;
import jakarta.servlet.http.HttpSession;



@Controller
public class EventController {
    @Autowired
    private PatientRepository patientRepo;
    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private GMailer gMailer;
 
    // go to patient event view
    @GetMapping("/patients/viewEvent")
    public String getEvent(Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }
        autoChangeEvent();
        categorizeEvent(model, patient.getUsername());
        return "patients/viewEventPage";
    }

    // book an event
    @PostMapping("/patients/addEvent")
    public String addEvent(@RequestParam String eventCode, Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        if (patient == null) {
            return "loginPage";
        }

        Event event = eventRepo.findByEventCode(eventCode);
        if (event.getCurrentNum() < event.getCapacity()) {
            patient.getEventsJoin().add(event.getEventCode());
            event.getPatients().add(patient.getUsername());
            event.setCurrentNum(event.getCurrentNum() + 1);
    
            patientRepo.save(patient); // Save patient to update the join table
            eventRepo.save(event); // Save event to update the current number

            try {
                gMailer.sendBookMail(patient, event);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        categorizeEvent(model, patient.getUsername());
        return "patients/viewEventPage";
    }


    // delete an booked event
    @PostMapping("/patients/deleteEvent")
    public String deleteEvent(@RequestParam String eventCode, Model model, HttpSession session) {
        Patient patient = (Patient) session.getAttribute("session_patient");
        Event event = eventRepo.findByEventCode(eventCode);
        if (patient == null) {
            return "loginPage";
        }

        try {
            gMailer.sendDeleteMail(patient, event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateDelete(patient, event);     
        categorizeEvent(model, patient.getUsername());
        return "patients/viewEventPage";
    }
    // helper function to update delete event 
    public void updateDelete(Patient patient, Event event) {
        List<String> patients = new ArrayList<>();
        List<String> events = new ArrayList<>();

        List<String> oldPatients = event.getPatients();
        List<String> oldEvents = patient.getEventsJoin();

        for (String s : oldPatients) {
            if (!s.equals(patient.getUsername())) {
                patients.add(s);
            }
        }

        for (String s : oldEvents) {
            if (!s.equals(event.getEventCode())) {
                events.add(s);
            }
        }

        patient.setEventsJoin(events);
        event.setPatients(patients);
        event.setCurrentNum(patients.size());

        patientRepo.save(patient); // Save patient to update the join table
        eventRepo.save(event); // Save event to update the current number
    
    }
    // helper function to check pass
    public void autoChangeEvent() {
        // check schedule time with current time 
        List<Event> events = eventRepo.findAll();
        //LocalDateTime current = LocalDateTime.now();
        LocalDateTime current = LocalDateTime.now().minusHours(7);
        

        for (Event event : events) {
            LocalDateTime evenDateTime = LocalDateTime.of(event.getDate().toLocalDate(), event.getStartTime().toLocalTime());
            if (evenDateTime.isBefore(current)) {
                event.setPast(true);
                eventRepo.save(event);
            }
        }
    }
    // helper function to find whether patient book that event
    public boolean checkBook(Event e, String username) {
        List<String> patients = e.getPatients();
        for (String p : patients) {
            if (p.equals(username)) {
                return true;
            }
        }
        return false;
    }

    // helper function to caegorize event list
    public void categorizeEvent(Model model, String username) {
        List<Event> bookEvents = new ArrayList<>();
        List<Event> unBookEvents = new ArrayList<>();
        List<Event> events = eventRepo.findAll();

        for (Event e : events) {
            if (!e.isPast()) {
                if (checkBook(e, username)) {
                    bookEvents.add(e);
                }
                else {
                    unBookEvents.add(e);
                }
            }
        
        }
        Collections.sort(bookEvents);
        Collections.sort(unBookEvents);
        model.addAttribute("booked", bookEvents);
        model.addAttribute("unbooked", unBookEvents);
    }
    
    

}
