package controllers.SVMController;


import libsvm.svm_node;

import static constants.SVMConstants.BPCoe;
import static constants.SVMConstants.BPGain;
import static constants.SVMConstants.BPNumSec;
import static constants.SVMConstants.NUM_BAND;
import static constants.SVMConstants.NUM_EEG_CH;
import static constants.SVMConstants.N_START_BAND;
import static constants.SVMConstants.SAMPLE_RATE;
import static constants.SVMConstants.WINDOW_LENGTH;
import static constants.SVMConstants.kCompMat;
import static constants.SVMConstants.kCompMat2;
import static constants.SVMConstants.nComp;
import static constants.SVMConstants.overLap;
import static constants.SVMConstants.preFilterA;
import static constants.SVMConstants.preFilterB;

public class SVM_Helper {


    public SVM_Helper() {
    }

    /**
     * Convert raw eeg from muse to features for predict.
     *
     * @param rawEEG Raw EEG from Muse
     * @return Extracted Features from raw EEG
     */
    public double[][] rawToFeature(double[][] rawEEG) {
        double[][] fEEGData = artifactRemoval(rawEEG);
        double[][] extractedFeatures = extractFeatures(fEEGData);

        return extractedFeatures;
    }

    public svm_node[] featuresToSVMNode(double[] features) {

        svm_node[] svmNode = new svm_node[features.length*2];

        int len = features.length;

        for (int i = 0; i < len; i++) {
            svmNode[i] = new svm_node();
            svmNode[i].index = i;
            svmNode[i].value = features[i];

            svmNode[i+features.length] = new svm_node();
            svmNode[i+features.length].index = i+len;
            svmNode[i+features.length].value = 0;

        }

        return svmNode;
    }

    public void filterIIR(double[] filt_b, double[] filt_a, double[][] data, int ch) {
        int Nback = filt_b.length;
        double[] prev_y = new double[Nback];
        double[] prev_x = new double[Nback];

        //step through data points
        for (int i = 0; i < data.length; i++) {
            //shift the previous outputs
            for (int j = Nback-1; j > 0; j--) {
                prev_y[j] = prev_y[j-1];
                prev_x[j] = prev_x[j-1];
            }

            //add in the new point
            prev_x[0] = data[i][ch];

            //compute the new data point
            double out = 0;
            for (int j = 0; j < Nback; j++) {
                out += filt_b[j]*prev_x[j];
                if (j > 0) {
                    out -= filt_a[j]*prev_y[j];
                }
            }

            //save output value
            prev_y[0] = out;
            data[i][ch] = (float) out;
        }
    }

    public void filterIIR(double[] filt_b, double[] filt_a, double[][][] data, int ch, int band) {
        int Nback = filt_b.length;
        double[] prev_y = new double[Nback];
        double[] prev_x = new double[Nback];

        //step through data points
        for (int i = 0; i < data.length; i++) {
            //shift the previous outputs
            for (int j = Nback - 1; j > 0; j--) {
                prev_y[j] = prev_y[j - 1];
                prev_x[j] = prev_x[j - 1];
            }

            //add in the new point
            prev_x[0] = data[i][ch][band];

            //compute the new data point
            double out = 0;
            for (int j = 0; j < Nback; j++) {
                out += filt_b[j] * prev_x[j];
                if (j > 0) {
                    out -= filt_a[j] * prev_y[j];
                }
            }

            //save output value
            prev_y[0] = out;
            data[i][ch][band] = (float) out;
        }
    }

    /**
     * Artifact Removal
     * Assuming window size of 512 x 4
     * @param fInEEGData EEG window
     */
    public double[][] artifactRemoval(double[][] fInEEGData) {

        for (int i=0; i<NUM_EEG_CH; i++){
            filterIIR(preFilterB, preFilterA, fInEEGData, i);
        }

        // Array of fInEEGData should be 512 x 4
        double[][] fRefData = new double[fInEEGData.length][NUM_EEG_CH];

        double[][] fOutEEGData = new double[fInEEGData.length][NUM_EEG_CH];

        int dataLength = fInEEGData.length;

        // Artifact Removal
        //ncomp for moving average for windows
        for (int j = 0; j < nComp; j++) { //Loop 14 times..

            for (int k = 0; k < (fInEEGData.length - kCompMat[j]); k++) { //Loop until not enough EEG to mean
                //Getting mean
                //fRefData (k,:) = mean(fInEEGData(k:k+para.kCompMat(j)-1,:));
                for (int ch = 0; ch < NUM_EEG_CH; ch++) {
                    fRefData[k][ch] = getMean(k, (k + kCompMat[j]), ch, fInEEGData);
                }
            }

            int index = kCompMat2[j];
                for (int i=0;i<(dataLength-kCompMat2[j]);i++){
                    for (int ch = 0; ch < NUM_EEG_CH; ch++) {
                        fOutEEGData[i][ch] = fInEEGData[index][ch] - fRefData[i][ch] + fOutEEGData[i][ch];
                    }
                    index++;
                }

        }

        //    fOutEEGData = fOutEEGData/para.nComp;
        for (int row = 0; row < fOutEEGData.length; row++) {
            for (int col = 0; col < NUM_EEG_CH; col++) {
                fOutEEGData[row][col] = fOutEEGData[row][col] / nComp;
            }
        }

        return fOutEEGData;
    }

    public double[][] extractFeatures(double[][] xm) {

        double fs = SAMPLE_RATE;
        double winLen = WINDOW_LENGTH;

        int winSize = (int) Math.floor(winLen * fs); // window size = 2 * 256 = 512

        int winShift = (int) Math.floor(winSize * (100 - overLap) / 100); //sample overlap 0.5 * 512 = 256

        int numSeg = (int) Math.floor((xm.length - winSize) / winShift) + 1;

        int numChannel = xm[0].length;

        int nband = NUM_BAND;

        double[][] xWinFeature = new double[numSeg][nband * numChannel];
        double[][][] xm_filtered;// = new double[xm.length][xm[0].length][nband]; //Create 3D array last one 6 for 6 different bands

        //bandpass filter them into different bands
        xm_filtered = bandPassFilter(xm);
        double[][][] xWinFeature1 = new double[numSeg][numChannel][nband]; //segment, ch x 4, band x 6

        for (int iSeg = 0; iSeg < numSeg; iSeg++) {

            int xStart = (iSeg) * winShift;// + 1;
            int xEnd = (iSeg) * winShift + winSize;

            for (int iCh = 0; iCh < numChannel; iCh++) {
                for (int band = 0; band < nband; band++) {
                    xWinFeature1[iSeg][iCh][band] = mySum(xm_filtered, xStart, xEnd, iCh, band); //CTG: relative power
                }

                double mySumOfSqueeze = mySqueeze(xWinFeature1, iSeg, iCh);
                for (int band = 0; band < nband; band++) {
                    xWinFeature1[iSeg][iCh][band] = xWinFeature1[iSeg][iCh][band] / mySumOfSqueeze;
                }

            }

            int iFeat = 0;
            for (int j = 0; j < numChannel; j++) {
                for (int m = 0; m < nband; m++) {
                    xWinFeature[iSeg][iFeat] = xWinFeature1[iSeg][j][m];
                    iFeat++;
                }
            }
        }

        return xWinFeature;

    }

    public double[][][] bandPassFilter(double[][] xm) {

        int nband = NUM_BAND;
        double[][][] xm_filtered = new double[xm.length][xm[0].length][nband];

        for (int band = (N_START_BAND - 1); band < nband; band++) {
            for (int i = 0; i < xm_filtered.length; i++) {
                for (int j = 0; j < xm_filtered[i].length; j++) {
                    xm_filtered[i][j][band] = xm[i][j];
                }
            }

            int nSection = BPNumSec[band];
            double[][] fCoe = BPCoe[band];
            double[] fGain = BPGain[band];

            for (int j = 0; j < nSection; j++) {
                double[] B = setAB_for_filter(fCoe[j], 0, 2);
                double[] A = setAB_for_filter(fCoe[j], 3, 5);

                xm_filtered = bpfHelper(fGain[j], B, A, xm_filtered, band);
            }


        }


        return xm_filtered;
    }

    private double getMean(int kStart, int end, int col, double[][] fInEEGData) {
        double sum = 0;
        for (int i = kStart; i < end; i++) {
            sum += fInEEGData[i][col];
        }

        return sum / (double) (end - kStart);
    }

    private double mySqueeze(double[][][] xWinFeature1, int iSeg, int iCh) {
        int sum = 0;

        for (int i = 0; i < NUM_BAND; i++) {
            sum += xWinFeature1[iSeg][iCh][i];
        }
        return sum;
    }

    private double mySum(double[][][] xm_filtered, int xStart, int xEnd, int iCh, int band) {

        double sum = 0;
        for (int i = xStart; i < xEnd; i++) {
            sum += Math.pow(xm_filtered[i][iCh][band], 2);
        }

        return sum;
    }

    private double[][][] bpfHelper(double fGain, double[] B, double[] A, double[][][] xm_filtered, int band) {

        for (int ch = 0; ch < NUM_EEG_CH; ch++) {
            filterIIR(B, A, xm_filtered, ch, band);
        }

        for (int i = 0; i < xm_filtered.length; i++) {
            for (int ch = 0; ch < NUM_EEG_CH; ch++) {
                xm_filtered[i][ch][band] = xm_filtered[i][ch][band] * fGain;
            }
        }
        return xm_filtered;
    }

    private double[] setAB_for_filter(double[] fCoe, int start, int end) {
        double[] result = new double[3];

        int index = 0;
        for (int i = start; i <= end; i++) {
            result[index++] = fCoe[i];
        }

        return result;
    }
}
