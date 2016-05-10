package au.org.intersect.faims.android.util;

/**
 * Created by Matthew on 10/05/2016.
 */

@Singlton
public class TestRunID {
    private static final TestRunID instance = new TestRunID();

    private TestRunID(){}

    public static TestRunID getInstance() {
        return instance;
    }

    private String runID = "";

    public String getRunID() {
        return runID;
    }

    public void setRunID(String runID) {
        this.runID = runID;
    }
}
