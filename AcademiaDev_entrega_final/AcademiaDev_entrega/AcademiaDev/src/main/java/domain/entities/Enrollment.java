package domain.entities;

import domain.exceptions.BusinessException;

public class Enrollment {
    private final String id;
    private final Student student;
    private final Course course;
    private int progress;
    private boolean active;

    public Enrollment(String id, Student student, Course course) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.progress = 0;
        this.active = true;
    }

    public void updateProgress(int newProgress) {
        if (newProgress < 0 || newProgress > 100) {
            throw new BusinessException("O progresso deve estar entre 0 e 100.");
        }
        this.progress = newProgress;
    }

    public void cancel() {
        this.active = false;
    }

    public String getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public int getProgress() { return progress; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        return String.format("Matrícula[%s] Aluno: %s | Curso: %s | Progresso: %d%% | Ativa: %s",
                id, student.getName(), course.getTitle(), progress, active ? "Sim" : "Não");
    }
}
