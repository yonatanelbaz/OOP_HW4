package tests.Classroom;

import solution.Given;
import solution.Then;
import solution.When;
import org.junit.Assert;

import java.util.Date;

public class ClassroomStoryTest {
    private java.util.Date reconstructionTime;
    private String nameOfClassroom;
    private tests.Classroom.Chairs chairs;

    protected tests.Classroom.Classroom classroom;

    @Given("a classroom with a capacity of &capacity")
    public void aClassroom(Integer capacity) {
        classroom = new tests.Classroom.Classroom(capacity);
        reconstructionTime = new Date();
        nameOfClassroom = "Capacity-" + capacity;
        chairs = new tests.Classroom.Chairs(capacity);
    }

    @When("the number of students in the classroom is &size")
    public void classroomIsWithStudents(Integer size) {
        classroom.numberOfStudents(size);
    }

    @Then("the classroom is &condition")
    public void theClassromCondition(String condition) {
        Assert.assertEquals(condition, classroom.classroomCondition());
    }
}
