package controllers.SVMController;


import java.lang.reflect.Array;
import java.util.Arrays;

import controllers.SVMController.uk.me.berndporr.iirj.Butterworth;

import static constants.SVMConstants.*;

public class SVM_Helper {

    Butterworth bw;

    public SVM_Helper() {
        // Setup ButterWorth
        // Calculate centerFrequency ==> https://electronics.stackexchange.com/questions/234752/why-is-center-frequency-of-a-bandpass-filter-is-given-by-the-geometric-average-o
        bw = new Butterworth();
        bw.bandPass(F_ORDER, SAMPLE_RATE, CENTER_FREQUENCY, WIDTH_FREQUENCY); //Setup bandpass filter.
    }

    private double getMean(int start, int end, int col, double [][] fInEEGData){

        double sum=0;
        for (int i=start; i<end; i++){
            sum+=fInEEGData[i][col];
        }

        return sum/(end-start);
    }

    private double[][] getEEGOut(double[][] fInEEGData, double[][] fOutEEGData){



        return fOutEEGData;
    }

    /**
     * Artifact Removal
     * Assuming window size of 512 x 4
     * @param fInEEGData EEG window
     */

    public double[][] artifactRemoval(double[][] fInEEGData) {

        // Bandpass Filter
        for (int i=0; i<fInEEGData.length;i++){
            for(int k=0; k<fInEEGData[i].length; k++){
                fInEEGData[i][k] = bw.filter(fInEEGData[i][k]);
            }
        }

        // Array of fInEEGData should be 512 x 4 todo better way to init the 2D square array?
        double[][] fRefData = new double[fInEEGData.length][NUM_EEG_CH];
        Arrays.fill(fRefData, 0);

        double[][] fOutEEGData = new double[fInEEGData.length][NUM_EEG_CH];
        Arrays.fill(fRefData, 0);

        int dataLength = fInEEGData.length;

        // Artifact Removal
        for (int j=0; j<nComp; j++){ //Loop 14 times.. TODO what is nComp for?
            for (int k=0; k<=(fInEEGData.length-kCompMat[j]); k++){ //Loop until not enough EEG to mean
                //Getting mean
                //fRefData (k,:) = mean(fInEEGData(k:k+para.kCompMat(j)-1,:));

                for (int col=0; col<NUM_EEG_CH; col++){
                    fRefData[k][col] = getMean(k, k+kCompMat[j]-1,col, fInEEGData);
                }

                /*
                    fOutEEGData(1:datalength-para.kCompMat2(j),:) = ...
                    fInEEGData(para.kCompMat2(j)+1:datalength,:) - ...
                    fRefData(1:datalength-para.kCompMat2(j),:) + ...
                    fOutEEGData (1:datalength-para.kCompMat2(j),:);
                */

                for (int i=0;i<(dataLength-kCompMat2[j]);i++){
                    for (int col=0;col<NUM_EEG_CH;col++){
                        fOutEEGData[i][col] = fInEEGData[kCompMat2[j]+1][col] - fRefData[i][col] + fOutEEGData[i][col];
                    }
                }
            }
        }

        //    fOutEEGData = fOutEEGData/para.nComp;
        for(int row=0; row<fOutEEGData.length;row++){
            for(int col=0; col<NUM_EEG_CH;col++){
                fOutEEGData[row][col] = fOutEEGData[row][col] / nComp;
            }
        }

        return fOutEEGData;
    }

    public static void extractFeatures(double[][] xm){
        /*
        %==  Initialize parameters
        fs = para.fs;
        winLen = para.winLen;     %lzq: window length, 3 seconds
                overlap = para.overlap;
        winTime = [];
        winStart = [];
        winEnd = [];
        winIdx = 1;
        */

        double fs = SAMPLE_RATE;
        double winLen = WINDOW_LENGTH;
        double winIdx = 1;
        double[] winTime;
        double[] winStart;

        double winSize = Math.floor(fs*fs); // window size = 2 * 256 = 512
        double winShift = Math.floor(winSize*(100-overLap/100)); //sample overlap 0.5 * 512 = 256
        double numSeg = Math.floor( (xm.length - winSize) / winShift);
        double numChannel = xm[0].length;

        double nband = NUM_BAND;



    }
}
