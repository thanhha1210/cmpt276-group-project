package cmpt276.group.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cmpt276.group.demo.models.admin.AdminRepository;
import cmpt276.group.demo.models.doctor.DoctorRepository;
import cmpt276.group.demo.models.patient.PatientRepository;
import cmpt276.group.demo.models.record.RecordRepository;

@Controller
public class DoctorController {
  @Autowired
  private DoctorRepository doctors;

  @Autowired
  private PatientRepository patients;

  @Autowired
  private AdminRepository admins;

  @Autowired
  private RecordRepository record;

}
