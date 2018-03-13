package constants;

public final class SVMConstants {

    // Bandpass Filter
    public final static double SAMPLE_RATE = 256; // Sample Frequency
    public final static int F_ORDER = 4; //Preprocessing Bandpass-filter signals (0.3-45 Hz)
    public final static double FREQ_LOW = 0.3;
    public final static double FREQ_HIGH = 45;
    public final static double CENTER_FREQUENCY = Math.sqrt(FREQ_LOW * FREQ_HIGH);
    public final static double WIDTH_FREQUENCY = FREQ_HIGH - FREQ_LOW;


    public final static int NUM_EEG_CH = 4;
    public final static int[] kCompMat = {22,24,26,28,30,32,34,36,38,40,42,44,48,52};
    public final static int[] kCompMat2 = {11,12,13,14,15,16,17,18,19,20,21,22,24,26};
    public final static double nComp = kCompMat.length;

    /*
    %   para.bandWt = ones(length(para.band),1);  %lzq: create matrix (7, 1) (7 rows and 1 column) % CTG: removed

    % para.winLen is the window length for feature extraction from EEG
    para.winLen = 2;   %sec
    para.overlap = 50; %percentage
    para.maFeat = 1;%9; % averaging frames
    para.maStep = 1;%5; % shifting frames
    para.fs = 256; %sample period

    */
    public final static double WINDOW_LENGTH = 2;
    public final static double overLap = 50; //percentage
    public final static double maFeat = 1; //averaging frames
    public final static double maStep = 1; //shifting frames

    // setting parameter for bandpass feature extraction
    /*
    %   para.band = [4 8 12 16 24 48 64];
    para.band = [1 4 8 12 18 30 45];   %CTG: changed the band range
    para.nband = length(para.band) - 1;
    para.nstartband = 1; %start from theta band - removin delta band which is noisy
    */

    public final static double[] BAND = {1, 4, 8, 12, 18, 30, 45}; //TODO theta is 3-8hz?
    public final static double NUM_BAND = BAND.length-1;
    public final static double NUM_START_BAND = 1; //start from theta band - removing delta band which is noisy


}
