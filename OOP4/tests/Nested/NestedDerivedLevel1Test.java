package tests.Nested;

import solution.When;

public class NestedDerivedLevel1Test extends NestedTest {
    @When("level1 add &num")
    private void addNum(Integer num) {
        this.calculator.addNum(num);
    }


}
