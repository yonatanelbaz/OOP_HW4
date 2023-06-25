package solution;

import provided.StoryTestException;


public class    StoryTestExceptionImpl extends StoryTestException {

    private static final long serialVersionUID = 1L;
    private String sentence;
    private String storyExpected;
    private String testResult;
    private int numFail;

    //initialize the exception with the relevant data
    public StoryTestExceptionImpl(String storyExpected,String testResult ,String sentence, int numFail) {
        this.sentence = sentence;
        this.storyExpected = storyExpected;
        this.testResult = testResult;
        this.numFail = numFail;
    }

    //getters
    @Override
    public String getSentance() {
        return sentence;
    }

    @Override
    public String getStoryExpected() {
        return storyExpected;
    }

    @Override
    public String getTestResult() {
        return testResult;
    }

    @Override
    public int getNumFail() {
        return numFail;
    }
}
