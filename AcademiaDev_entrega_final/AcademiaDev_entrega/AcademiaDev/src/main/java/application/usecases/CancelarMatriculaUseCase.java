package application.usecases;

import application.repositories.EnrollmentRepository;
import application.repositories.UserRepository;
import domain.entities.Enrollment;
import domain.entities.Student;
import domain.entities.User;
import domain.exceptions.BusinessException;

import java.util.List;

public class CancelarMatriculaUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public CancelarMatriculaUseCase(EnrollmentRepository enrollmentRepository,
                                     UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    public void execute(String studentId, String enrollmentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Aluno não encontrado."));

        if (!(user instanceof Student student)) {
            throw new BusinessException("Usuário não é um aluno.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        Enrollment enrollment = enrollments.stream()
                .filter(e -> e.getId().equals(enrollmentId) && e.isActive())
                .findFirst()
                .orElseThrow(() -> new BusinessException("Matrícula ativa não encontrada."));

        enrollment.cancel();
    }
}
