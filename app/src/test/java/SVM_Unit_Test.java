import com.opencsv.CSVReader;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import controllers.SVMController.SVM_Helper;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import static constants.JUnitTestConstants.DELTA;
import static constants.JUnitTestConstants.sampleSize;
import static constants.SVMConstants.NUM_BAND;
import static constants.SVMConstants.NUM_EEG_CH;
import static constants.SVMConstants.preFilterA;
import static constants.SVMConstants.preFilterB;
import static junit.framework.Assert.assertFalse;

public class SVM_Unit_Test {

    private SVM_Helper sh = new SVM_Helper();
    private String eeg_raw_fn = "raw_eeg.csv";
    private String eeg_after_filter_fn = "eeg_after_filter.csv";
    private String eeg_after_ar_fn = "eeg_after_ar.csv";
    private String eeg_extract_features_fn = "ExtractFeaturesMatlab.csv";

    @Test
    public void testFilterIIRFunction() throws Exception {
        double[][] java_bandpass_eeg = csv_reader(eeg_raw_fn, sampleSize, NUM_EEG_CH);

        for (int i = 0; i < NUM_EEG_CH; i++) {
            sh.filterIIR(preFilterB, preFilterA, java_bandpass_eeg, i);
        }

        double[][] matlab_eeg_after_filter = csv_reader(eeg_after_filter_fn, sampleSize, NUM_EEG_CH);
        compare_array_with_delta(matlab_eeg_after_filter, java_bandpass_eeg);

    }

    @Test
    public void testArtifactRemoval() throws Exception {

//        double[][] eegRaw = deep_copy_2d(EEG_RAW);
        double[][] eegRaw = csv_reader(eeg_raw_fn, sampleSize, NUM_EEG_CH);
        double[][] eeg_ar_java = sh.artifactRemoval(eegRaw);
        double[][] eeg_matlab_after_ar = csv_reader(eeg_after_ar_fn, sampleSize, NUM_EEG_CH);
        compare_array_with_delta(eeg_matlab_after_ar, eeg_ar_java);

    }

    @Test
    public void testExtractFeatures() throws Exception {
        SVM_Helper sh = new SVM_Helper();

        double[][] eeg = csv_reader(eeg_after_ar_fn, sampleSize, NUM_EEG_CH);
        double[][] extractedFeatures = sh.extractFeatures(eeg);

        double[][] matlabExtractFeatures = csv_reader(eeg_extract_features_fn, 10, 24);

        compare_array_with_delta(matlabExtractFeatures, extractedFeatures);

        System.out.print("");


    }

    @Test
    public void testBandpassFilter() throws Exception {
        double[][] xm = csv_reader(eeg_after_ar_fn, sampleSize, NUM_EEG_CH);
        double[][][] xmFiltered = sh.bandPassFilter(xm);
        String filename = "extractFeature_after_bandpass.csv";
        double[][][] matlab = csv_reader(filename, sampleSize, NUM_EEG_CH, NUM_BAND);
        compare_array_with_delta(matlab, xmFiltered);
    }

    @Test //TODO
    public void loadSVMmodel() throws Exception {
        svm_model svmModel = new svm_model();
        String fn = "svm_model.txt";
        InputStream is = getClass().getClassLoader().getResourceAsStream(fn);

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        svmModel = svm.svm_load_model(br);

        svm_node[] node = new svm_node[1];
        node[0] = new svm_node();
        node[0].index = 1;
        node[0].value = 0.4617394630959994;

        double[] a = {1, 2};
        // Meditation: 0, Stress: 1
//        double result  = svm.svm_predict(svmModel, node);
        double result = svm.svm_predict_probability(svmModel, node, a);
        System.out.println("result = " + result);
    }


    private double[][] deep_copy_2d(double[][] array) {
        double[][] copiedArray = new double[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(array[i], 0, copiedArray[i], 0, array[0].length);
        }
        return copiedArray;
    }

    private void compare_array_with_delta(double[][] matlabArray, double[][] javaArray) {
        for (int i = 0; i < matlabArray.length; i++) {
            for (int j = 0; j < matlabArray[i].length; j++) {
                Boolean result = Math.abs(matlabArray[i][j] - javaArray[i][j]) > DELTA;
                if (result) {
                    System.out.println("Error Location = i=" + i + "\tj=" + j);
                    System.out.println("Matlab:\t" + matlabArray[i][j] + "\nJava:\t" + javaArray[i][j]);

                }
                assertFalse(result);
            }
        }
    }

    private void compare_array_with_delta(double[][][] matlabArray, double[][][] javaArray) {
        for (int i = 0; i < matlabArray.length; i++) {
            for (int j = 0; j < matlabArray[i].length; j++) {
                for (int k = 0; k < matlabArray[i][j].length; k++) {
                    Boolean result = Math.abs(matlabArray[i][j][k] - javaArray[i][j][k]) > DELTA;
                    if (result) {
                        System.out.println("Error Location = i=" + i + "\tj=" + j);
                        System.out.println("matlab =\t" + matlabArray[i][j][k] + "\njava =   \t" + javaArray[i][j][k]);

                    }
                    assertFalse(result);

                }
            }
        }
    }

    /***
     *
     * @param filename
     * @param ch
     * @param band
     * @return
     */
    private double[][][] csv_reader(String filename, int size, int ch, int band) {

        double[][][] a = new double[size][ch][band];

        Reader reader = null;
        CSVReader csvReader = null;
        try {
            reader = new InputStreamReader(getClass().getResourceAsStream(filename));
            csvReader = new CSVReader(reader);
            String[] nextRecord;
            int count;
            int index = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                count = 0;
                for (int k = 0; k < band; k++) {
                    for (int j = 0; j < ch; j++) { //Shift the Ch first
                        a[index][j][k] = Double.parseDouble(nextRecord[count++]);
                    }
                }
                System.out.println();
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;

    }

    private double[][] csv_reader(String filename, int size, int ch) {

        double[][] new_array = new double[size][ch];

        Reader reader = null;
        CSVReader csvReader = null;
        try {
            reader = new InputStreamReader(getClass().getResourceAsStream(filename));
            csvReader = new CSVReader(reader);
            String[] nextRecord;
            int count;
            int index = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                count = 0;
                for (int j = 0; j < ch; j++) {
                    new_array[index][j] = Double.parseDouble(nextRecord[count++]);
                }
                System.out.println();
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new_array;

    }
}


    

