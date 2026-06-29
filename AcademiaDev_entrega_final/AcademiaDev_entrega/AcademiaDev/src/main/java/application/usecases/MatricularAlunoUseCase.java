package application.usecases;

import application.repositories.CourseRepository;
import application.repositories.EnrollmentRepository;
import application.repositories.UserRepository;
import domain.entities.*;
import domain.exceptions.BusinessException;
import domain.exceptions.EnrollmentException;

import java.util.UUID;

public class MatricularAlunoUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public MatricularAlunoUseCase(EnrollmentRepository enrollmentRepository,
                                   CourseRepository courseRepository,
                                   UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Enrollment execute(String studentId, String courseId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Aluno não encontrado: " + studentId));

        if (!(user instanceof Student student)) {
            throw new BusinessException("Usuário não é um aluno.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Curso não encontrado: " + courseId));

        if (!course.isActive()) {
            throw new EnrollmentException("O curso '" + course.getTitle() + "' está inativo e não aceita novas matrículas.");
        }

        enrollmentRepository.findByStudentAndCourse(student, courseId).ifPresent(e -> {
            if (e.isActive()) {
                throw new EnrollmentException("O aluno já está matriculado no curso '" + course.getTitle() + "'.");
            }
        });

        long activeCount = enrollmentRepository.countActiveByStudent(student);
        if (!student.canEnroll((int) activeCount)) {
            throw new EnrollmentException("O plano " + student.getSubscriptionPlan().getPlanName()
                    + " não permite mais matrículas ativas (limite: 3).");
        }

        Enrollment enrollment = new Enrollment(UUID.randomUUID().toString(), student, course);
        enrollmentRepository.save(enrollment);
        return enrollment;
    }
}
