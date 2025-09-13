package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.StudentRequest;
import com.iseem_backend.application.DTO.request.CustomFieldRequest;
import com.iseem_backend.application.DTO.response.StudentResponse;
import com.iseem_backend.application.mapper.StudentMapper;
import com.iseem_backend.application.model.CustomField;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.Student;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.repository.StudentRepository;
import com.iseem_backend.application.repository.UserRepository;
import com.iseem_backend.application.service.StudentService;
import com.iseem_backend.application.utils.CardGenerator;
import com.iseem_backend.application.utils.ExcelUtils;
import com.iseem_backend.application.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;

    private Set<CustomField> mapCustomFieldsToEntity(Set<CustomFieldRequest> requests, Student student) {
        if (requests == null) return new HashSet<>();
        Set<CustomField> fields = new HashSet<>();
        for (CustomFieldRequest req : requests) {
            CustomField field = new CustomField();
            field.setFieldName(req.getFieldName());
            field.setFieldValue(req.getFieldValue());
            field.setStudent(student);
            fields.add(field);
        }
        return fields;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public StudentResponse ajouterEtudiant(UUID userId, StudentRequest studentRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (studentRepository.existsById(userId)) {
            throw new RuntimeException("User is already a student");
        }

        Student student = studentMapper.toEntity(studentRequest);
        student.setUser(user);

        if (studentRequest.getCustomFields() != null) {
            student.setCustomFields(mapCustomFieldsToEntity(studentRequest.getCustomFields(), student));
        }

        user.setRole(Role.ETUDIANT);
        userRepository.save(user);

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public StudentResponse modifierEtudiant(UUID id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        if (!student.getMatricule().equals(request.getMatricule())) {
            studentRepository.findByMatricule(request.getMatricule())
                    .ifPresent(existingStudent -> {
                        throw new RuntimeException("Matricule " + request.getMatricule() + " déjà utilisée par un autre étudiant");
                    });
        }

        studentMapper.updateEntityFromRequest(request, student);

        if (request.getCustomFields() != null) {
            student.getCustomFields().clear();
            student.getCustomFields().addAll(mapCustomFieldsToEntity(request.getCustomFields(), student));
        }

        Student updated = studentRepository.save(student);
        return studentMapper.toDto(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void supprimerEtudiant(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        User user = student.getUser();
        studentRepository.delete(student);

        user.setRole(Role.ETUDIANT);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('PROFESSEUR') or (hasRole('ETUDIANT') and authentication.name == #id.toString())")
    public StudentResponse consulterProfil(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public StudentResponse obtenirEtudiantParMatricule(String matricule) {
        Student student = studentRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Étudiant avec matricule " + matricule + " non trouvé"));
        return studentMapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('PROFESSEUR')")
    public Page<StudentResponse> obtenirTousLesEtudiants(Pageable pageable) {
        Page<Student> students = studentRepository.findAll(pageable);
        return students.map(studentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('PROFESSEUR')")
    public List<StudentResponse> rechercherEtudiants(String nom, String prenom, String matricule) {
        List<Student> students = studentRepository.findByUserNomContainingIgnoreCaseAndUserPrenomContainingIgnoreCaseAndMatriculeContainingIgnoreCase(nom, prenom, matricule);
        return students.stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMINISTRATION','ENSEIGNANT')")
    public List<StudentResponse> getStudentsForTeacher(UUID teacherId) {
        List<Student> students = studentRepository.findAll(); // fetch all students

        return students.stream()
                .filter(student -> student.getModules().stream()
                        .anyMatch(module -> module.getEnseignant() != null && module.getEnseignant().getEnseignantId().equals(teacherId)))
                .map(studentMapper::toDto)
                .toList();
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public Map<String, Object> importerEtudiants(MultipartFile fichierExcel) {
        List<Student> students = ExcelUtils.importStudents(fichierExcel);

        List<Student> successfulImports = new ArrayList<>();
        List<String> duplicateEmails = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Student student : students) {
            try {
                if (userRepository.existsByEmail(student.getUser().getEmail())) {
                    duplicateEmails.add(student.getUser().getEmail());
                    continue;
                }

                if (studentRepository.existsByMatricule(student.getMatricule())) {
                    duplicateEmails.add(student.getMatricule() + " (matricule)");
                    continue;
                }

                User savedUser = userRepository.save(student.getUser());
                student.setUser(savedUser);

                if (student.getCustomFields() != null) {
                    student.setCustomFields(mapCustomFieldsToEntity(
                            student.getCustomFields().stream().map(cf -> new CustomFieldRequest(cf.getFieldName(), cf.getFieldValue())).collect(Collectors.toSet()),
                            student
                    ));
                }

                Student savedStudent = studentRepository.save(student);
                successfulImports.add(savedStudent);

            } catch (Exception e) {
                log.error("Error saving student {}: {}", student.getUser().getEmail(), e.getMessage());
                errors.add(String.format("Error importing %s: %s", student.getUser().getEmail(), e.getMessage()));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalProcessed", students.size());
        result.put("successfulImports", successfulImports.size());
        result.put("duplicates", duplicateEmails.size());
        result.put("errors", errors.size());
        result.put("duplicateEmails", duplicateEmails);
        result.put("errorMessages", errors);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public byte[] exporterEtudiants() {
        List<Student> students = studentRepository.findAll();
        return ExcelUtils.exportStudents(students);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public byte[] genererCartesScolaires(List<UUID> idsEtudiants) {
        List<Student> students = studentRepository.findAllById(idsEtudiants);
        return CardGenerator.generateCards(students);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void ajouterDiplome(UUID idEtudiant, Diplome diplome) {
        Student student = studentRepository.findById(idEtudiant)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + idEtudiant));
        diplome.setStudent(student);
        student.getDiplomes().add(diplome);
        studentRepository.save(student);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void supprimerDiplome(UUID idEtudiant, UUID idDiplome) {
        Student student = studentRepository.findById(idEtudiant)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + idEtudiant));
        Diplome diplomeToRemove = student.getDiplomes().stream()
                .filter(d -> d.getIdDiplome().equals(idDiplome))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Diplome not found with id: " + idDiplome));
        student.getDiplomes().remove(diplomeToRemove);
        studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public List<Diplome> consulterDiplomes(UUID idEtudiant) {
        Student student = studentRepository.findById(idEtudiant)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + idEtudiant));
        return new ArrayList<>(student.getDiplomes());
    }
}
