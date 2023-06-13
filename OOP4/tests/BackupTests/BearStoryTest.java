package tests.BackupTests;

import solution.Given;
import solution.Then;
import solution.When;
import org.junit.Assert;

public class BearStoryTest {
	private tests.BackupTests.Bear bear;
	
	@Given("a bear of age &age")
	public void aBear(Integer age) {
		bear = new tests.BackupTests.Bear(age);
	}
	
	@When("he hunts for the duration of &hours")
	public void heHunts(Integer hours) {
		bear.hunt(hours);
	}

	@When("he sleeps for the duration of &hours")
	public void heSleeps(Integer hours) {
		bear.sleep(hours);
	}
	
	@Then("he feels &feeling")
	public void heFeels(String feeling) {
		Assert.assertEquals(feeling, bear.getFeeling());
	}
}
