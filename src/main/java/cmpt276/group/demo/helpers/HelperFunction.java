package cmpt276.group.demo.helpers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cmpt276.group.demo.models.appointment.Appointment;
import cmpt276.group.demo.models.appointment.AppointmentRepository;
import cmpt276.group.demo.models.past_appointment.PastAppointment;
import cmpt276.group.demo.models.past_appointment.PastAppointmentRepository;

public class HelperFunction {

    @Autowired
    private static AppointmentRepository appointmentRepo;

    @Autowired
    private static PastAppointmentRepository pastAppointmentRepo;

    public static void changeApt() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        System.out.println(currentDate);

        // Retrieve all appointments
        List<Appointment> appointmentList = appointmentRepo.findAll();

        // Loop through appointments and update status
        for (Appointment appointment : appointmentList) {
            // if the 
            if (appointment.getDate().toLocalDate().isBefore(currentDate)) {
                // Create a new PastAppointment
                PastAppointment pastAppointment = 
                new PastAppointment(appointment.getDoctorName(), appointment.getDoctorUsername(),
                                    appointment.getPatientName(), appointment.getPatientUsername(),
                                    appointment.getDate(), appointment.getStartTime(),
                                    appointment.getDuration(), appointment.getDepartment());

                // Add to pastApt
                pastAppointmentRepo.save(pastAppointment);

                // Delete from Apt
                appointmentRepo.delete(appointment);
            }
        }
    }
}