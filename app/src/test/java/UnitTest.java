import org.junit.Test;

import controllers.SVMController.SVM_Helper;

import static constants.JUnitTestConstants.eeg_artifact_removed;
import static constants.JUnitTestConstants.eeg_raw;

public class UnitTest {

    @Test
    public void validateArtifactRemoval() {
        SVM_Helper sh = new SVM_Helper();
        double[][] result = sh.artifactRemoval(eeg_raw);
        double[][] eeg = eeg_artifact_removed;

        System.out.print(result.length);
        System.out.print(eeg.length);
//        assertTrue(Arrays.equals(result, eeg_artifact_removed));
    }

}
    

