package tests.BackupTests;

import solution.Given;
import solution.Then;
import solution.When;
import org.junit.Assert;

public class LionStoryTest {
	private tests.BackupTests.Lion lion;
	
	@Given("a lion of age &age")
	public void aLion(Integer age) {
		lion = new tests.BackupTests.Lion(age);
	}
	
	@When("he hunts for the duration of &hours")
	public void heHunts(Integer hours) {
		lion.hunt(hours);
	}

	@When("he sleeps for the duration of &hours")
	public void heSleeps(Integer hours) {
		lion.sleep(hours);
	}
	
	@Then("he feels &feeling")
	public void heFeels(String feeling) {
		Assert.assertEquals(feeling, lion.getFeeling());
	}
}
