package infrastructure.persistence;

import application.repositories.CourseRepository;
import domain.entities.Course;
import domain.enums.DifficultyLevel;

import java.util.*;
import java.util.stream.Collectors;

public class CourseRepositoryEmMemoria implements CourseRepository {

    // Map com title como chave garante unicidade por título
    private final Map<String, Course> cursosPorTitulo = new LinkedHashMap<>();
    // Map auxiliar por id
    private final Map<String, Course> cursosPorId = new LinkedHashMap<>();

    @Override
    public void save(Course course) {
        cursosPorTitulo.put(course.getTitle(), course);
        cursosPorId.put(course.getId(), course);
    }

    @Override
    public Optional<Course> findById(String id) {
        return Optional.ofNullable(cursosPorId.get(id));
    }

    @Override
    public Optional<Course> findByTitle(String title) {
        return Optional.ofNullable(cursosPorTitulo.get(title));
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(cursosPorId.values());
    }

    @Override
    public List<Course> findByDifficultyLevel(DifficultyLevel level) {
        return cursosPorId.values().stream()
                .filter(c -> c.getDifficultyLevel() == level)
                .collect(Collectors.toList());
    }
}
