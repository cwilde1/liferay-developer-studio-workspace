package com.university.course;

public class CourseResponse {
    private long courseId;
    private String courseName;
    private String description;
    private int credits;
    private int students;

    public CourseResponse(long courseId, String courseName, String description, int credits, int students) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.students = students;
    }

    // Getters and setters
    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getStudents() { return students; }
    public void setStudents(int students) { this.students = students; }
}