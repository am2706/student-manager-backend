package com.college.sms.repository;

import com.college.sms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);

    @Query("""
           SELECT s FROM Student s
           WHERE LOWER(s.firstName)  LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(s.lastName)   LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(s.email)      LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(s.department) LIKE LOWER(CONCAT('%', :q, '%'))
           """)
    List<Student> search(@Param("q") String q);
}
