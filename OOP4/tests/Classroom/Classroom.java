package tests.Classroom;

public class Classroom {
    private Integer freeSpace;


    public Classroom(Integer capacity) {
        this.freeSpace = capacity;
    }

    public Classroom(Classroom classroom) {
        this.freeSpace = classroom.freeSpace;
    }

    public void numberOfStudents(Integer numberOfStudents) {
        this.freeSpace -= numberOfStudents;
    }

    public void broeknChairs(Integer numberOfBrokenChairs) {
        this.freeSpace -= numberOfBrokenChairs;
    }

    public String classroomCondition() {
        return this.freeSpace > 0 ? "not-full" : "full";
    }
}
