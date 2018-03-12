package constants;

public final class SVMConstants {

    // Bandpass Filter
    public final static double SAMPLE_RATE = 256; // Sample Frequency
    public final static int F_ORDER = 4; //Preprocessing Bandpass-filter signals (0.3-45 Hz)
    public final static double FREQ_LOW = 0.3;
    public final static double FREQ_HIGH = 45;


    public final static int[] kCompMat = {22,24,26,28,30,32,34,36,38,40,42,44,48,52};
    public final static int[] kCompMat2 = {11,12,13,14,15,16,17,18,19,20,21,22,24,26};
    public final static int nComp = kCompMat.length;

}
