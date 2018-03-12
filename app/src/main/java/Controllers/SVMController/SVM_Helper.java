package Controllers.SVMController;

import Controllers.SVMController.uk.me.berndporr.iirj.Butterworth;

import static constants.SVMConstants.*;

/**
 * Created by zhijie on 8/3/18.
 */

public class SVM_Helper {

    Butterworth bw;

    public SVM_Helper() {
        // Setup ButterWorth
        // Calculate centerFrequency ==> https://electronics.stackexchange.com/questions/234752/why-is-center-frequency-of-a-bandpass-filter-is-given-by-the-geometric-average-o
        bw = new Butterworth();
        bw.bandPass(F_ORDER, SAMPLE_RATE, Math.sqrt(FREQ_LOW * FREQ_HIGH), FREQ_HIGH - FREQ_LOW);
    }

    /**
     * Artifact Removal
     * Assuming window size of 512 x 4
     * @param fInEEGData EEG window
     */

    public void artifactRemoval(double[][] fInEEGData) {

        // Bandpass Filter
        for (int i=0; i<fInEEGData.length;i++){
            for(int k=0; k<fInEEGData[i].length; k++){
                fInEEGData[i][k] = bw.filter(fInEEGData[i][k]);
            }
        }

        // Array of fInEEGData should be 512 x 4 todo better way to init 2nd variable?
        double[][] fRefData = new double[fInEEGData.length][fInEEGData[0].length];
        // Artifact Removal
        for (int j=0; j<nComp; j++){
            for (int k=0; k<(fInEEGData.length-kCompMat[j]); j++){

            }

        }

    }

    public static void extractFeatures(){

    }
}
