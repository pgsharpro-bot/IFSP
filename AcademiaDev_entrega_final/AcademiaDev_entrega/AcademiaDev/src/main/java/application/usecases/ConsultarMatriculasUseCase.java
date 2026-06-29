package application.usecases;

import application.repositories.EnrollmentRepository;
import application.repositories.UserRepository;
import domain.entities.Enrollment;
import domain.entities.Student;
import domain.entities.User;
import domain.exceptions.BusinessException;

import java.util.List;

public class ConsultarMatriculasUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public ConsultarMatriculasUseCase(EnrollmentRepository enrollmentRepository,
                                       UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    public List<Enrollment> execute(String studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Aluno não encontrado."));

        if (!(user instanceof Student student)) {
            throw new BusinessException("Usuário não é um aluno.");
        }

        return enrollmentRepository.findByStudent(student);
    }
}
