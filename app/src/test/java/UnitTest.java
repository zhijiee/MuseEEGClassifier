import org.junit.Test;

import java.util.Arrays;

import controllers.SVMController.SVM_Helper;

import static constants.JUnitTestConstants.eeg_artifact_removed;
import static constants.JUnitTestConstants.eeg_raw;
import static org.junit.Assert.assertTrue;

public class UnitTest {

    @Test
    public void validateArtifactRemoval() {
        SVM_Helper sh = new SVM_Helper();
        double[][] result = sh.artifactRemoval(eeg_raw);
        assertTrue(Arrays.equals(result, eeg_artifact_removed));
    }

}
    

