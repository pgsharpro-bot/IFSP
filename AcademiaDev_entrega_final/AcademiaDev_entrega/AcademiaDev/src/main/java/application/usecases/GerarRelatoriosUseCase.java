package application.usecases;

import application.repositories.CourseRepository;
import application.repositories.EnrollmentRepository;
import application.repositories.UserRepository;
import domain.entities.*;
import domain.enums.DifficultyLevel;

import java.util.*;
import java.util.stream.Collectors;

public class GerarRelatoriosUseCase {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public GerarRelatoriosUseCase(CourseRepository courseRepository,
                                   EnrollmentRepository enrollmentRepository,
                                   UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    /** Relatório 1: cursos por nível de dificuldade, ordenados alfabeticamente */
    public List<Course> cursosPorNivel(DifficultyLevel level) {
        return courseRepository.findByDifficultyLevel(level).stream()
                .sorted(Comparator.comparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /** Relatório 2: instrutores únicos com cursos ACTIVE */
    public Set<String> instrutoresDeAtivos() {
        return courseRepository.findAll().stream()
                .filter(Course::isActive)
                .map(Course::getInstructorName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /** Relatório 3: alunos agrupados por plano de assinatura */
    public Map<String, List<Student>> alunosPorPlano() {
        return userRepository.findAllStudents().stream()
                .collect(Collectors.groupingBy(s -> s.getSubscriptionPlan().getPlanName()));
    }

    /** Relatório 4: média geral de progresso de todas as matrículas */
    public double mediaGeralProgresso() {
        return enrollmentRepository.findAll().stream()
                .mapToInt(Enrollment::getProgress)
                .average()
                .orElse(0.0);
    }

    /** Relatório 5: aluno com maior número de matrículas ativas */
    public Optional<Student> alunoComMaisMatriculas() {
        return userRepository.findAllStudents().stream()
                .max(Comparator.comparingLong(s ->
                        enrollmentRepository.countActiveByStudent(s)));
    }

    /** Retorna todas as matrículas (para exportação CSV) */
    public List<Enrollment> todasMatriculas() {
        return enrollmentRepository.findAll();
    }

    /** Retorna todos os cursos (para exportação CSV) */
    public List<Course> todosCursos() {
        return courseRepository.findAll();
    }

    /** Retorna todos os alunos (para exportação CSV) */
    public List<Student> todosAlunos() {
        return userRepository.findAllStudents();
    }
}
