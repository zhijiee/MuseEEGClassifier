import com.opencsv.CSVReader;

import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import controllers.SVMController.SVM_Helper;

import static constants.JUnitTestConstants.DELTA;
import static constants.JUnitTestConstants.EEG_AFTER_ARTIFACT_REMOVAL;
import static constants.SVMConstants.NUM_EEG_CH;
import static constants.SVMConstants.preFilterA;
import static constants.SVMConstants.preFilterB;
import static junit.framework.Assert.assertFalse;

public class SVM_Unit_Test {

    static SVM_Helper sh = new SVM_Helper();
    static String eeg_raw_fn = "/Volumes/SandiskSD/DevelopmentWorkspace/AndroidDevelopment/MuseEEGClassifier/app/src/test/java/assets/raw_eeg.csv";
    static String eeg_after_filter_fn = "/Volumes/SandiskSD/DevelopmentWorkspace/AndroidDevelopment/MuseEEGClassifier/app/src/test/java/assets/eeg_after_filter.csv";
    static String eeg_after_ar_fn = "/Volumes/SandiskSD/DevelopmentWorkspace/AndroidDevelopment/MuseEEGClassifier/app/src/test/java/assets/eeg_after_ar.csv";
    static String eeg_extract_features_fn = "/Volumes/SandiskSD/DevelopmentWorkspace/AndroidDevelopment/MuseEEGClassifier/app/src/test/java/assets/ExtractFeaturesMatlab.csv";

    @Test
    public void testFilterIIRFunction() throws Exception {
//        double[][] java_bandpass_eeg = deep_copy_2d(EEG_RAW);
        double[][] java_bandpass_eeg = csv_reader(eeg_raw_fn, 1024, 4);

        for (int i = 0; i < NUM_EEG_CH; i++) {
            sh.filterIIR(preFilterB, preFilterA, java_bandpass_eeg, i);
        }
        double[][] eeg_after_filter = csv_reader(eeg_after_filter_fn, 1024, 4);
        compare_array_with_delta(java_bandpass_eeg, eeg_after_filter);

    }

    @Test
    public void testArtifactRemoval() throws Exception {

//        double[][] eegRaw = deep_copy_2d(EEG_RAW);
        double[][] eegRaw = csv_reader(eeg_raw_fn, 1024, 4);
        double[][] eeg_ar_java = sh.artifactRemoval(eegRaw);
        double[][] eeg_matlab_after_ar = csv_reader(eeg_after_ar_fn, 1024, 4);
        compare_array_with_delta(eeg_ar_java, eeg_matlab_after_ar);

    }

    @Test
    public void testExtractFeatures() throws Exception {
        SVM_Helper sh = new SVM_Helper();

//        double[][] eeg = deep_copy_2d(EEG_AFTER_ARTIFACT_REMOVAL);
        double[][] eeg = csv_reader(eeg_after_ar_fn, 1024, 4);
        double[][] extractedFeatures = sh.extractFeatures(eeg);

        double[][] matlabExtractFeatures = csv_reader(eeg_extract_features_fn, 2, 24);

        compare_array_with_delta(extractedFeatures, matlabExtractFeatures);

        System.out.print("");


    }

    @Test
    public void testBandpassFilter() throws Exception {
        double[][] xm = deep_copy_2d(EEG_AFTER_ARTIFACT_REMOVAL);
        double[][][] xmFiltered = sh.bandPassFilter(xm);
        String filename = "/Volumes/SandiskSD/DevelopmentWorkspace/AndroidDevelopment/MuseEEGClassifier/app/src/test/java/assets/extractFeature_after_bandpass.csv";
        double[][][] matlab = csv_reader(filename, 256, 4, 6);
        compare_array_with_delta(xmFiltered, matlab);
    }


    private double[][] deep_copy_2d(double[][] array) {
        double[][] copiedArray = new double[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(array[i], 0, copiedArray[i], 0, array[0].length);
        }
        return copiedArray;
    }

    private void compare_array_with_delta(double[][] array1, double[][] array2) {
        for (int i = 0; i < array1.length; i++) {
            for (int j = 0; j < array1[i].length; j++) {
                Boolean result = Math.abs(array1[i][j] - array2[i][j]) > DELTA;
                if (result) {
                    System.out.println("Error Location = i=" + i + "\tj=" + j);
                    System.out.println("Array1 = " + array1[i][j] + "\nArray2 = " + array2[i][j]);

                }
                assertFalse(result);
            }
        }
    }

    private void compare_array_with_delta(double[][][] array1, double[][][] array2) {
        for (int i = 0; i < array1.length; i++) {
            for (int j = 0; j < array1[i].length; j++) {
                for (int k = 0; k < array1[i][j].length; k++) {
                    Boolean result = Math.abs(array1[i][j][k] - array2[i][j][k]) > DELTA;
                    if (result) {
                        System.out.println("Error Location = i=" + i + "\tj=" + j);
                        System.out.println("Array1 = " + array1[i][j] + "\nArray2 = " + array2[i][j]);

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
            reader = Files.newBufferedReader(Paths.get(filename));
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
            reader = Files.newBufferedReader(Paths.get(filename));
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


    

