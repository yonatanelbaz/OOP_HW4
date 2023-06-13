package tests;


import provided.GivenNotFoundException;
import provided.StoryTestException;
import provided.StoryTester;
import solution.StoryTesterImpl;
import org.junit.Before;
import org.junit.Test;
import tests.Nested.NestedDerivedLevel3Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class CalculatorTest {

    private StoryTester tester;
    private String goodStoryMultiInheritance;
    private String badStoryMultiInheritance;
    private String goodStoryMultiOr;
    private String badStoryShort;
    private String innerGoodStory;
    private String subInnerGoodStory;
    private Class<?> testClass;
    private Class<?> derivedTestClass;

    @Before
    public void setUp() {
        goodStoryMultiInheritance = "Given number 75\n"
                + "When level0 add 10\n"
                + "When level1 add 10\n"
                + "When level2 add 5\n"
                + "When level3 add 5\n"
                + "Then result 105";

        badStoryMultiInheritance = "Given number 75\n"
                + "When level0 add 10\n"
                + "When level1 add 10\n"
                + "When level2 add 5\n"
                + "When level3 add 5\n"
                + "Then result 45 or result 30 or result 10 or result 100 or result 99";

        badStoryShort = "Given number 75\n"
                + "When level0 add 10\n"
                + "When level1 add 10\n"
                + "When level2 add 5\n"
                + "When level3 add 5\n"
                + "Then result 45 or result 30 or result 105 or result 100 or result 99\n"

                + "When level2 minus 30\n"
                + "When level3 add 20\n"
                + "When level2 add 4\n"
                + "When level1 add 6\n"
                + "Then result 45 or result 30\n"

                + "When level1 add 6\n"
                + "Then result 45 or result 30 or result 25\n"

                + "When level1 add 20\n"
                + "When level3 add 4\n"
                + "When level3 add 6\n"
                + "When level2 minus 30\n"
                + "Then result 45 or result 30 or result 105";

        goodStoryMultiOr = "Given number 75\n"
                + "When level0 add 10\n"
                + "When level1 add 10\n"
                + "When level2 add 5\n"
                + "When level3 add 5\n"
                + "Then result 45 or result 30 or result 105 or result 100 or result 99\n"

                + "When level2 minus 30\n"
                + "When level3 add 20\n"
                + "When level2 add 4\n"
                + "When level1 add 6\n"
                + "Then result 45 or result 30\n"

                + "When level1 add 6\n"
                + "Then result 45 or result 30 or result 25\n"

                + "When level1 add 20\n"
                + "When level3 add 4\n"
                + "When level3 add 6\n"
                + "When level2 minus 30\n"
                + "Then result 45 or result 30 or result 105\n"

                + "When level0 add 20\n"
                + "When level3 add 6\n"
                + "Then result 25"
        ;

        innerGoodStory = "Given level21 0\n"
                + "When level21 add 10\n"
                + "When level21 add 20\n"
                + "When level2 add 10\n"
                + "Then result 50 or result21 40"
        ;

        subInnerGoodStory = "Given level221 0\n"
                + "When level221 add 10\n"
                + "When level3 add 20\n"
                + "When level2 add 10\n"
                + "When level1 add 10\n"
                + "When level0 add 10\n"
                + "Then result 10 or result 20 or result 60 or result 30"
        ;

        testClass = CalculatorTest.class;
        derivedTestClass = NestedDerivedLevel3Test.class;
        tester = new StoryTesterImpl();
    }


    @Test
    public void testMultiInheritance() throws Exception {
        try {
            tester.testOnInheritanceTree(goodStoryMultiInheritance, derivedTestClass);
            assertTrue(true);
        } catch (StoryTestException e) {
            fail();
        }

        try {
            tester.testOnInheritanceTree(badStoryShort, derivedTestClass);
            fail();
        } catch (StoryTestException e) {
            assertEquals("Then result 45 or result 30", e.getSentance());
            assertEquals(Arrays.asList("45", "30"), e.getTestResult());
            assertEquals(Arrays.asList("105", "105"), e.getStoryExpected());
            assertEquals(3, e.getNumFail());
        }

        try {
            tester.testOnInheritanceTree(goodStoryMultiOr, derivedTestClass);
            fail();
        } catch (StoryTestException e) {
            assertEquals("Then result 45 or result 30", e.getSentance());
            assertEquals(Arrays.asList("45", "30"), e.getTestResult());
            assertEquals(Arrays.asList("105", "105"), e.getStoryExpected());
            assertEquals(4, e.getNumFail());
        }
    }

    @Test
    public void testMultiNested() throws Exception {
        try {
            tester.testOnNestedClasses(goodStoryMultiInheritance, derivedTestClass);
            assertTrue(true);
        } catch (StoryTestException e) {
            fail();
        }

        try {
            tester.testOnNestedClasses(badStoryMultiInheritance, derivedTestClass);
            assertTrue(true);
        } catch (StoryTestException e) {
            assertEquals("Then result 45 or result 30 or result 10 or result 100 or result 99", e.getSentance());
            assertEquals(Arrays.asList("45", "30", "10", "100", "99"), e.getTestResult());
            assertEquals(Arrays.asList("105", "105", "105", "105", "105"), e.getStoryExpected());
            assertEquals(1, e.getNumFail());
        }

        try {
            tester.testOnNestedClasses(goodStoryMultiOr, derivedTestClass);
            fail();
        } catch (StoryTestException e) {
            assertEquals("Then result 45 or result 30", e.getSentance());
            assertEquals(Arrays.asList("45", "30"), e.getTestResult());
            assertEquals(Arrays.asList("105", "105"), e.getStoryExpected());
            assertEquals(4, e.getNumFail());
        }

        try {
            tester.testOnNestedClasses(innerGoodStory, derivedTestClass);
            fail();
        } catch (GivenNotFoundException e) {
            assertTrue(true);
        }

        try {
            tester.testOnNestedClasses(subInnerGoodStory, derivedTestClass);
            fail();
        } catch (GivenNotFoundException e) {
            assertTrue(true);
        }
    }
}
