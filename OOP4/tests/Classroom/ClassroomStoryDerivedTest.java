package tests.Classroom;

import solution.Given;
import solution.When;

public class ClassroomStoryDerivedTest extends tests.Classroom.ClassroomStoryTest {

    @When("the number of broken chairs in the classroom is &broken")
    private void classroomIsWIthBrokenChairs(Integer broken) {
        classroom.broeknChairs(broken);
    }

    public class InnerClass extends tests.Classroom.ClassroomStoryTest {
        @Given("a classroom that the number of seats in it is &seats")
        public void aClassroom(Integer seats) {
            classroom = new tests.Classroom.Classroom(seats);
        }

        @When("the number of students in the classroom is &students and the number among them that are standing is &standing")
        private void classroomIsWIthStandingStudents(Integer students, Integer standing) {
            classroom.numberOfStudents(students - standing);
        }
    }
}
