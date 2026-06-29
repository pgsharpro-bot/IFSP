package application.usecases;

import application.repositories.CourseRepository;
import domain.entities.Course;

import java.util.List;
import java.util.stream.Collectors;

public class ConsultarCatalogoUseCase {

    private final CourseRepository courseRepository;

    public ConsultarCatalogoUseCase(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> listarCursosAtivos() {
        return courseRepository.findAll().stream()
                .filter(Course::isActive)
                .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                .collect(Collectors.toList());
    }

    public List<Course> listarTodos() {
        return courseRepository.findAll();
    }
}
