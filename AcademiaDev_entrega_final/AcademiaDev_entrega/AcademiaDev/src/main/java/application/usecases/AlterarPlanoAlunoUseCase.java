package application.usecases;

import application.repositories.UserRepository;
import domain.entities.*;
import domain.exceptions.BusinessException;

public class AlterarPlanoAlunoUseCase {

    private final UserRepository userRepository;

    public AlterarPlanoAlunoUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Student execute(String studentId, String planName) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Aluno não encontrado: " + studentId));

        if (!(user instanceof Student student)) {
            throw new BusinessException("Usuário não é um aluno.");
        }

        SubscriptionPlan plan = switch (planName.toUpperCase()) {
            case "BASIC", "BASICPLAN" -> new BasicPlan();
            case "PREMIUM", "PREMIUMPLAN" -> new PremiumPlan();
            default -> throw new BusinessException("Plano inválido: " + planName + ". Use BASIC ou PREMIUM.");
        };

        student.setSubscriptionPlan(plan);
        return student;
    }
}
