package application.repositories;

import domain.entities.Enrollment;
import domain.entities.Student;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Optional<Enrollment> findById(String id);
    List<Enrollment> findAll();
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findActiveByStudent(Student student);
    long countActiveByStudent(Student student);
    Optional<Enrollment> findByStudentAndCourse(Student student, String courseId);
}
