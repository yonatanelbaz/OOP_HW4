package tests;


import provided.*;
import solution.StoryTesterImpl;
import org.junit.Before;
import org.junit.Test;
import tests.Classroom.ClassroomStoryDerivedTest;
import tests.Classroom.ClassroomStoryTest;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class ClassroomTest {

    private StoryTester tester;
    private String goodStory;
    private String goodStoryInheritance;
    private String goodStoryNested;
    private String longGoodStory;
    private String badStoryRestore;
    private String badStoryThenWithAnd;
    private String badStorySingle;
    private String badStoryMulti;
    private String derivedStory;
    private String nestedStory;
    private Class<?> testClass;
    private Class<?> derivedTestClass;

    @Before
    public void setUp() {
        // capacity is 75
        goodStory = "Given a classroom with a capacity of 75\n"
                + "When the number of students in the classroom is 60\n"
                + "Then the classroom is not-full\n"
                + "When the number of students in the classroom is 10\n"
                + "Then the classroom is not-full\n"
                + "When the number of students in the classroom is 5\n"
                + "Then the classroom is full";

        // capacity is 75
        badStoryRestore = "Given a classroom with a capacity of 75\n"
                + "When the number of students in the classroom is 60\n"
                + "Then the classroom is not-full\n"
                + "When the number of students in the classroom is 15\n"
                + "Then the classroom is not-full\n"
                + "When the number of students in the classroom is 15\n"
                + "Then the classroom is full";

        goodStoryInheritance = "Given a classroom with a capacity of 50\n"
                + "When the number of students in the classroom is 40\n"
                + "When the number of broken chairs in the classroom is 10\n"
                + "Then the classroom is full";

        goodStoryNested = "Given a classroom that the number of seats in it is 1337\n"
                + "When the number of students in the classroom is 1378 and the number among the that are standing is 42\n"
                + "When the number of broken chairs in the classroom is 10\n"
                + "Then the classroom is not-full";

        testClass = ClassroomStoryTest.class;
        derivedTestClass = ClassroomStoryDerivedTest.class;
        tester = new StoryTesterImpl();
    }


    @Test
    public void testGoodStoriesBackup() throws Exception {
        try {
            tester.testOnInheritanceTree(goodStory, testClass);
            assertTrue(true);
        } catch (StoryTestException e) {
            fail();
        }
        try {
            tester.testOnInheritanceTree(badStoryRestore, testClass);
            fail();
        } catch (StoryTestException e) {
            assertEquals("Then the classroom is not-full", e.getSentance());
            assertEquals("not-full", e.getStoryExpected());
            assertEquals("full", e.getTestResult());
            assertEquals(1, e.getNumFail());
        }
    }

    @Test
    public void testGoodStoriesDerived() throws Exception {
        try {
            tester.testOnInheritanceTree(goodStoryInheritance, derivedTestClass);
            assertTrue(true);
        } catch (StoryTestException e) {
            fail();
        }

        try {
            tester.testOnNestedClasses(goodStoryInheritance, derivedTestClass);
            assertTrue(true);
        } catch (StoryTestException e) {
            fail();
        }

        try {
            tester.testOnInheritanceTree(goodStoryNested, derivedTestClass);
            fail();
        } catch (GivenNotFoundException e) {
            assertTrue(true);
        }
    }
}
