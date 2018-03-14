import org.junit.Test;

import java.util.Arrays;

import controllers.SVMController.SVM_Helper;

import static constants.JUnitTestConstants.*;
import static constants.SVMConstants.*;
import static java.lang.Math.abs;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SVM_Unit_Test {

    @Test
    public void validateBandpassFilter() throws Exception{
        SVM_Helper sh = new SVM_Helper();

        double[][] java_bandpass_eeg = EEG_RAW;
        for (int i=0; i<NUM_EEG_CH; i++){
            sh.filterIIR(preFilterB, preFilterA, java_bandpass_eeg, i);
        }

        double largest = 0;
        double delta = DELTA;

        for (int i=0; i<java_bandpass_eeg.length; i++){
            for (int j=0; j< NUM_EEG_CH; j++){
                // Ensure that the double value are close
                assertFalse( (abs(java_bandpass_eeg[i][j] - EEG_AFTER_BANDPASS[i][j])) > delta );
            }
        }
//        for(int i=0; i<java_bandpass_eeg.length;i++){
//            System.out.print(java_bandpass_eeg[i][0] + "\t");
//        }
//        System.out.println();
//
//        for(int i=0; i<java_bandpass_eeg.length;i++){
//            System.out.print(EEG_AFTER_BANDPASS[i][0] + "\t");
//        }
//        assertTrue(Arrays.equals(java_bandpass_eeg, EEG_AFTER_BANDPASS));

        sh=null;
    }


    @Test
    public void artifactRemoval() throws Exception {

        SVM_Helper sh = new SVM_Helper();
        double[][] eegRaw = EEG_RAW.clone();

        double[][] eeg_ar_java = sh.artifactRemoval(eegRaw);

        for(int i=0; i<eeg_ar_java.length; i++){
            System.out.println(eeg_ar_java[i][0]);
        }
//        for(int i=0; i<eeg_ar_java.length;i++){
//            System.out.print(eeg_ar_java[i][0] + "\t");
//        }
//        System.out.println();
//
//        for(int i=0; i<java_bandpass_eeg.length;i++){
//            System.out.print(EEG_AFTER_BANDPASS[i][0] + "\t");
//        }


//        sh=null;
    }


}
    

