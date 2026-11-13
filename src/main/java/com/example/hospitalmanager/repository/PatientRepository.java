package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
