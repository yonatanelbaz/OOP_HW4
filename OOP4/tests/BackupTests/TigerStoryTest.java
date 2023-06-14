package tests.BackupTests;

import solution.Given;
import solution.Then;
import solution.When;
import org.junit.Assert;

public class TigerStoryTest {
	private tests.BackupTests.Tiger tiger;
	
	@Given("a tiger of age &age")
	public void aTiger(Integer age) {
		tiger = new tests.BackupTests.Tiger(age);
	}
	
	@When("he hunts for the duration of &hours")
	public void heHunts(Integer hours) {
		tiger.hunt(hours);
	}

	@When("he sleeps for the duration of &hours")
	public void heSleeps(Integer hours) {
		tiger.sleep(hours);
	}
	
	@Then("he feels &feeling")
	public void heFeels(String feeling) {
		Assert.assertEquals(feeling, tiger.getFeeling());
	}
}
