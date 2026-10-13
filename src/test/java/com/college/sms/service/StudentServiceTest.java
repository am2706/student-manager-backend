package com.college.sms.service;

import com.college.sms.entity.Student;
import com.college.sms.exception.DuplicateResourceException;
import com.college.sms.exception.ResourceNotFoundException;
import com.college.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService service;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student();
        sampleStudent.setId(1L);
        sampleStudent.setRollNumber("CS2301");
        sampleStudent.setFirstName("Anitha");
        sampleStudent.setLastName("Reddy");
        sampleStudent.setEmail("anitha.reddy@college.edu");
        sampleStudent.setPhone("9876543210");
        sampleStudent.setDepartment("Computer Science");
        sampleStudent.setCourseYear(2);
    }

    @Test
    @DisplayName("findAll without search term returns all students")
    void testFindAll_NoSearch() {
        when(repository.findAll()).thenReturn(List.of(sampleStudent));

        List<Student> result = service.findAll(null);

        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll with search term calls repository search query")
    void testFindAll_WithSearch() {
        when(repository.search("Anitha")).thenReturn(List.of(sampleStudent));

        List<Student> result = service.findAll("Anitha");

        assertEquals(1, result.size());
        verify(repository, times(1)).search("Anitha");
    }

    @Test
    @DisplayName("findById returns student when ID exists")
    void testFindById_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        Student result = service.findById(1L);

        assertNotNull(result);
        assertEquals("CS2301", result.getRollNumber());
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when ID does not exist")
    void testFindById_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    @DisplayName("create saves student when roll number and email are unique")
    void testCreate_Success() {
        when(repository.existsByEmail("anitha.reddy@college.edu")).thenReturn(false);
        when(repository.existsByRollNumber("CS2301")).thenReturn(false);
        when(repository.save(any(Student.class))).thenReturn(sampleStudent);

        Student created = service.create(sampleStudent);

        assertNotNull(created);
        verify(repository, times(1)).save(sampleStudent);
    }

    @Test
    @DisplayName("create throws DuplicateResourceException when email is taken")
    void testCreate_DuplicateEmail() {
        when(repository.existsByEmail(sampleStudent.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(sampleStudent));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("delete removes student when found")
    void testDelete_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        doNothing().when(repository).delete(sampleStudent);

        service.delete(1L);

        verify(repository, times(1)).delete(sampleStudent);
    }
}
