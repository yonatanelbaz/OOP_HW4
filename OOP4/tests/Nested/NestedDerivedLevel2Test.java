package tests.Nested;

import solution.Given;
import solution.Then;
import solution.When;
import org.junit.Assert;

public class NestedDerivedLevel2Test extends NestedDerivedLevel1Test {
    @When("level2 add &num")
    private void addNum(Integer num) {
        this.calculator.addNum(num);
    }

    @When("level2 minus &num")
    private void subtractNum(Integer num) {
        this.calculator.subtractNum(num);
    }

    public class Inner21 extends NestedDerivedLevel2Test {
        @Given("level21 &number")
        private void initNumber(Integer num) {
            calculator = new Calculator(num);
        }

        @When("level21 add &num")
        private void addNum(Integer num) {
            this.calculator.addNum(num);
        }

        @Then("result21 &num")
        private void areEqualNums(Integer num) {
            Assert.assertEquals(calculator.getNum().toString(), num.toString());
        }
    }

    public class Inner22 extends NestedTest {
        @Given("level22 &number")
        private void initNumber(Integer num) {
            calculator = new Calculator(num);
        }

        @When("level22 add &num")
        private void addNum(Integer num) {
            this.calculator.addNum(num);
        }

        public class Inner221 extends NestedDerivedLevel3Test {
            public Inner221() {
            }

            @Given("level221 &number")
            private void initNumber(Integer num) {
                calculator = new Calculator(num);
            }

            @When("level221 add &num")
            private void addNum(Integer num) {
                this.calculator.addNum(num);
            }
        }
    }
}
