package application.usecases;

import application.repositories.CourseRepository;
import domain.entities.Course;
import domain.exceptions.BusinessException;

public class GerenciarStatusCursoUseCase {

    private final CourseRepository courseRepository;

    public GerenciarStatusCursoUseCase(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course ativar(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Curso não encontrado: " + courseId));
        course.activate();
        return course;
    }

    public Course inativar(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Curso não encontrado: " + courseId));
        course.deactivate();
        return course;
    }
}
