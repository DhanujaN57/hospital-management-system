
package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.MedicalRecordRequest;
import com.example.hospitalmanager.dto.MedicalRecordResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.MedicalRecord;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    // Create medical record
    public MedicalRecordResponse createMedicalRecord(
            MedicalRecordRequest request,
            Long doctorId
    ) {

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "Patient not found"
                        )
                );

        AppUser doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "Doctor not found"
                        )
                );

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "Only doctors can create medical records"
            );
        }

        MedicalRecord record = new MedicalRecord();

        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setDiagnosis(request.diagnosis());
        record.setNotes(request.notes());

        MedicalRecord savedRecord =
                medicalRecordRepository.save(record);

        return mapToResponse(savedRecord);
    }

    // Get all medical records
    public List<MedicalRecordResponse> getAllMedicalRecords() {

        return medicalRecordRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get medical records by patient ID
    public List<MedicalRecordResponse> getMedicalRecordsByPatient(
            Long patientId
    ) {

        if (!patientRepository.existsById(patientId)) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Patient not found"
            );
        }

        return medicalRecordRepository.findByPatient_Id(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get medical records by doctor ID
    public List<MedicalRecordResponse> getMedicalRecordsByDoctor(
            Long doctorId
    ) {

        return medicalRecordRepository.findByDoctor_Id(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Convert MedicalRecord entity into DTO
    private MedicalRecordResponse mapToResponse(
            MedicalRecord record
    ) {

        return new MedicalRecordResponse(
                record.getId(),
                record.getPatient().getId(),
                record.getPatient().getName(),
                record.getDoctor().getId(),
                record.getDoctor().getUsername(),
                record.getDiagnosis(),
                record.getNotes()
        );
    }
}
