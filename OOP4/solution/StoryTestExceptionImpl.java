package solution;

import provided.StoryTestException;

public class StoryTestExceptionImpl extends StoryTestException {

    private static final long serialVersionUID = 1L;
    private String sentence;
    private String storyExpected;
    private String testResult;
    private int numFail;

    public StoryTestExceptionImpl(String sentence, String storyExpected, String testResult, int numFail) {
        this.sentence = sentence;
        this.storyExpected = storyExpected;
        this.testResult = testResult;
        this.numFail = numFail;
    }

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
