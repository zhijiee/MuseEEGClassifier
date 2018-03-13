package controllers.SVMController;


import java.util.Arrays;

import controllers.SVMController.uk.me.berndporr.iirj.Butterworth;

import static constants.SVMConstants.BPCoe;
import static constants.SVMConstants.BPGain;
import static constants.SVMConstants.BPNumSec;
import static constants.SVMConstants.CENTER_FREQUENCY;
import static constants.SVMConstants.F_ORDER;
import static constants.SVMConstants.NUM_BAND;
import static constants.SVMConstants.NUM_EEG_CH;
import static constants.SVMConstants.N_START_BAND;
import static constants.SVMConstants.SAMPLE_RATE;
import static constants.SVMConstants.WIDTH_FREQUENCY;
import static constants.SVMConstants.WINDOW_LENGTH;
import static constants.SVMConstants.kCompMat;
import static constants.SVMConstants.kCompMat2;
import static constants.SVMConstants.nComp;
import static constants.SVMConstants.overLap;

public class SVM_Helper {

    Butterworth bw;

    public SVM_Helper() {
        // Setup ButterWorth
        // Calculate centerFrequency ==> https://electronics.stackexchange.com/questions/234752/why-is-center-frequency-of-a-bandpass-filter-is-given-by-the-geometric-average-o
        bw = new Butterworth();
        bw.bandPass(F_ORDER, SAMPLE_RATE, CENTER_FREQUENCY, WIDTH_FREQUENCY); //Setup bandpass filter.
    }

    private double getMean(int start, int end, int col, double[][] fInEEGData) {

        double sum=0;
        for (int i=start; i<end; i++){
            sum+=fInEEGData[i][col];
        }

        return sum/(end-start);
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
//        Arrays.fill(fRefData, 0.0);

        double[][] fOutEEGData = new double[fInEEGData.length][NUM_EEG_CH];
//        Arrays.fill(fOutEEGData, 0.0);

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

    public double[][] extractFeatures(double[][] xm) {
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

        int winSize = (int) Math.floor(fs * fs); // window size = 2 * 256 = 512
        int winShift = (int) Math.floor(winSize * (100 - overLap / 100)); //sample overlap 0.5 * 512 = 256
        int numSeg = (int) Math.floor( (xm.length - winSize) / winShift);
        int numChannel = xm[0].length;

        int nband = NUM_BAND;

        double[][] xWinFeature = new double[numSeg][nband * numChannel];
        double[][][] xm_filtered = new double[xm.length][xm[0].length][nband]; //Create 3D array last one 6 for 6 different bands
        Arrays.fill(xm_filtered, 0);

        //bandpass filter them into different bands
        xm_filtered = bandPassFilter(xm); //todo Stuck in band pass filter

        for (int iSeg = 0; iSeg < numSeg; iSeg++) {
            // xstart = (iSeg-1)*winShift +1;
            // xend = (iSeg-1)*winShift + winSz;

            int xStart = (iSeg) * winShift + 1;
            int xEnd = (iSeg) * winShift + winSize;


            double[][][] xWinFeature1 = new double[numSeg][numChannel][nband]; //segment, ch x 4, band x 6

            /*
                for iCh = 1: numChannel
                    xwinFeature1(iSeg,iCh,:) = sum(xm_filtered(xstart:xend,iCh,:).^2);  %CTG: relative power
                    xwinFeature1(iSeg,iCh,:) = xwinFeature1(iSeg,iCh,:)/sum(squeeze(xwinFeature1(iSeg,iCh,:)));  %CTG: relative power
                end
            */
            for (int iCh = 1; iCh < numChannel; iCh++) {
                for (int band = 0; band < nband; band++) {
                    xWinFeature1[iSeg][iCh][band] = mySum(xm_filtered, xStart, xEnd, iCh, band); //CTG: relative power
                }

                //TODO I don't see how squeeze affects this. So i am doing normal division. Can use same loop as above
                for (int band = 0; band < nband; band++) {
                    xWinFeature1[iSeg][iCh][band] = mySqueeze(xWinFeature1, iSeg, iCh, band);
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

    private double mySqueeze(double[][][] xWinFeature1, int iSeg, int iCh, int band) {
        int sum = 0;

        for (int i = 0; i < NUM_BAND; i++) {
            sum += xWinFeature1[iSeg][iCh][i];
        }

        return xWinFeature1[iSeg][iCh][band] / sum;
    }

    private double mySum(double[][][] xm_filtered, int xStart, int xEnd, int iCh, int band) {

        double sum = 0;
        for (int i = xStart; i < xEnd; i++) {
            sum += xm_filtered[i][iCh][band];
        }


        return Math.pow(sum, 2);
    }

    public double[][][] bandPassFilter(double[][] xm) {

        int nband = NUM_BAND;
        double[][][] xm_filtered = new double[xm.length][xm[0].length][nband];
        Arrays.fill(xm_filtered, 0);

        /*
        for i= para.nstartband: nband
            xm_filtered(:,:,i) = xm;
            nSection = para.BPNumSec(i);
            fCoe = para.BPCoe{i};
            fGain = para.BPGain{i};
            for j=1:nSection
                B = fCoe(j,1:3);
                A = fCoe(j,4:6);
                xm_filtered(:,:,i) = fGain(j)*filter(B,A,xm_filtered(:,:,i));
            end
        end
         */

        for (int band = N_START_BAND; band < nband; band++) {
            // xm_filtered(:,:,i) = xm;
            for (int i = 0; i < xm_filtered.length; i++) {
                for (int j = 0; j < xm_filtered[i].length; j++) {
                    xm_filtered[i][j][band] = xm[i][j];
                }
            }

            // nSection = para.BPNumSec(i);

            int nSection = BPNumSec[band];
            double[][] FCoe = BPCoe[band];
            double[] FGain = BPGain[band];

            for (int i = 0; i < nSection; i++) {
                //TODO Require some help with separating into different bands
            }


        }


        return xm_filtered;
    }
}
