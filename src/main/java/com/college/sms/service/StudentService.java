package com.college.sms.service;

import com.college.sms.entity.Student;
import com.college.sms.exception.DuplicateResourceException;
import com.college.sms.exception.ResourceNotFoundException;
import com.college.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;

    // Constructor-based dependency injection
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> findAll(String search) {
        if (search == null || search.isBlank()) return repository.findAll();
        return repository.search(search.trim());
    }

    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }

    @Transactional
    public Student create(Student student) {
        if (repository.existsByEmail(student.getEmail()))
            throw new DuplicateResourceException("A student with this email already exists");
        if (repository.existsByRollNumber(student.getRollNumber()))
            throw new DuplicateResourceException("A student with this roll number already exists");
        student.setId(null);
        return repository.save(student);
    }

    @Transactional
    public Student update(Long id, Student incoming) {
        Student existing = findById(id);

        // Check if email was changed to an already existing email
        if (!existing.getEmail().equalsIgnoreCase(incoming.getEmail()) && repository.existsByEmail(incoming.getEmail())) {
            throw new DuplicateResourceException("A student with this email already exists");
        }
        // Check if roll number was changed to an already existing roll number
        if (!existing.getRollNumber().equalsIgnoreCase(incoming.getRollNumber()) && repository.existsByRollNumber(incoming.getRollNumber())) {
            throw new DuplicateResourceException("A student with this roll number already exists");
        }

        existing.setRollNumber(incoming.getRollNumber());
        existing.setFirstName(incoming.getFirstName());
        existing.setLastName(incoming.getLastName());
        existing.setEmail(incoming.getEmail());
        existing.setPhone(incoming.getPhone());
        existing.setDepartment(incoming.getDepartment());
        existing.setCourseYear(incoming.getCourseYear());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
