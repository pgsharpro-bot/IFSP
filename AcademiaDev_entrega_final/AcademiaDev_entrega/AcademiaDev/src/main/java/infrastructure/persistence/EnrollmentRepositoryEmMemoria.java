package infrastructure.persistence;

import application.repositories.EnrollmentRepository;
import domain.entities.Enrollment;
import domain.entities.Student;

import java.util.*;
import java.util.stream.Collectors;

public class EnrollmentRepositoryEmMemoria implements EnrollmentRepository {

    private final Map<String, Enrollment> matriculas = new LinkedHashMap<>();

    @Override
    public void save(Enrollment enrollment) {
        matriculas.put(enrollment.getId(), enrollment);
    }

    @Override
    public Optional<Enrollment> findById(String id) {
        return Optional.ofNullable(matriculas.get(id));
    }

    @Override
    public List<Enrollment> findAll() {
        return new ArrayList<>(matriculas.values());
    }

    @Override
    public List<Enrollment> findByStudent(Student student) {
        return matriculas.values().stream()
                .filter(e -> e.getStudent().getId().equals(student.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Enrollment> findActiveByStudent(Student student) {
        return matriculas.values().stream()
                .filter(e -> e.getStudent().getId().equals(student.getId()) && e.isActive())
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveByStudent(Student student) {
        return matriculas.values().stream()
                .filter(e -> e.getStudent().getId().equals(student.getId()) && e.isActive())
                .count();
    }

    @Override
    public Optional<Enrollment> findByStudentAndCourse(Student student, String courseId) {
        return matriculas.values().stream()
                .filter(e -> e.getStudent().getId().equals(student.getId())
                          && e.getCourse().getId().equals(courseId))
                .findFirst();
    }
}
