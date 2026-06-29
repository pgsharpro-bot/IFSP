package domain.entities;

import domain.enums.CourseStatus;
import domain.enums.DifficultyLevel;

public class Course {
    private final String id;
    private final String title;
    private final String description;
    private final String instructorName;
    private final int durationInHours;
    private final DifficultyLevel difficultyLevel;
    private CourseStatus status;

    public Course(String id, String title, String description, String instructorName,
                  int durationInHours, DifficultyLevel difficultyLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.instructorName = instructorName;
        this.durationInHours = durationInHours;
        this.difficultyLevel = difficultyLevel;
        this.status = CourseStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == CourseStatus.ACTIVE;
    }

    public void activate() {
        this.status = CourseStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CourseStatus.INACTIVE;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorName() { return instructorName; }
    public int getDurationInHours() { return durationInHours; }
    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public CourseStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %dh | %s",
                id, title, instructorName, difficultyLevel, durationInHours, status);
    }
}
