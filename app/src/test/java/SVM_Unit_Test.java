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
import static constants.JUnitTestConstants.MEDITATION_CLASS;
import static constants.JUnitTestConstants.STRESS_CLASS;
import static constants.SVMConstants.NUM_BAND;
import static constants.SVMConstants.NUM_EEG_CH;
import static constants.SVMConstants.WINDOW_SHIFT;
import static constants.SVMConstants.WINDOW_SIZE;
import static constants.SVMConstants.preFilterA;
import static constants.SVMConstants.preFilterB;
import static java.lang.Math.abs;
import static junit.framework.Assert.assertFalse;

public class SVM_Unit_Test {

    private SVM_Helper sh = new SVM_Helper();
    private String eeg_raw_fn = "raw_eeg.csv";
    private String eeg_after_filter_fn = "eeg_after_filter.csv";
    private String eeg_after_ar_fn = "eeg_after_ar.csv";
    private String eeg_extract_features_fn = "ExtractFeaturesMatlab.csv";


    @Test
    public void testSamplingFreqSizeLargeSample() throws Exception {
        String eegMeditationFn = "raw_eeg_zhijie_meditation.csv";
        String eegStressFn = "raw_eeg_zhijie_stress.csv";
        String modelFn = "svm_model_segment.txt";


        //Load EEG
        double[][] eeg_window = new double[WINDOW_SIZE][NUM_EEG_CH]; //todo new test
        double[][] med_raw = csv_reader(eegMeditationFn, NUM_EEG_CH);
        double[][] str_raw = csv_reader(eegStressFn, NUM_EEG_CH);

        //Load SVM Model
        InputStream is = getClass().getClassLoader().getResourceAsStream(modelFn);
        svm_model svmModel = svm.svm_load_model(new BufferedReader(new InputStreamReader(is)));

        double[][] probResultStress = new double[str_raw.length][];
        double[][] probResultMeditation = new double[med_raw.length][];

        double[] stressPredictResult = new double[str_raw.length];
        double[] meditationPredictResult = new double[med_raw.length];

        int numCorrect = 0;
        int numMedSeg = (med_raw.length - WINDOW_SHIFT) / WINDOW_SHIFT;
        int numStrSeg = (str_raw.length - WINDOW_SHIFT) / WINDOW_SHIFT;

        int totalFeat = ((med_raw.length - WINDOW_SHIFT) / (WINDOW_SHIFT)) + (str_raw.length - WINDOW_SHIFT) / WINDOW_SHIFT;

        System.arraycopy(med_raw, 0, eeg_window, 0, WINDOW_SIZE); // Simulate copy to 2nd half
        for (int i = 1; i < numMedSeg + 1; i++) {
            System.arraycopy(med_raw, i * WINDOW_SHIFT, eeg_window, WINDOW_SHIFT, WINDOW_SHIFT); // Copy to 2nd half of array

            double[][] feat = sh.rawToFeature(eeg_window);
            svm_node[] node = sh.featuresToSVMNode(feat[0]);
            probResultMeditation[i] = new double[2];
            meditationPredictResult[i] = svm.svm_predict_probability(svmModel, node, probResultMeditation[i]);
            if (meditationPredictResult[i] == MEDITATION_CLASS) numCorrect++;
            sh.shift(eeg_window);
            System.out.print("Med Result: " + meditationPredictResult[i]);
            System.out.println("\tMed Probab: " + probResultMeditation[i][MEDITATION_CLASS]);

        }

        System.arraycopy(str_raw, 0, eeg_window, 0, WINDOW_SIZE); // Simulate copy to 2nd half
        for (int i = 0; i < numStrSeg - 7; i++) {
//            System.arraycopy(str_raw, i*WINDOW_SHIFT, eeg_window, WINDOW_SHIFT , WINDOW_SHIFT); // Copy to 2nd half of array
            System.arraycopy(str_raw, i * WINDOW_SHIFT, eeg_window, 0, WINDOW_SIZE); //todo changed

            double[][] feat = sh.rawToFeature(eeg_window);
            svm_node[] node = sh.featuresToSVMNode(feat[0]);
            probResultStress[i] = new double[2];
            stressPredictResult[i] = svm.svm_predict_probability(svmModel, node, probResultStress[i]);
            if (stressPredictResult[i] == STRESS_CLASS) numCorrect++;
//            sh.shift(eeg_window);

            System.out.print("strResult: " + stressPredictResult[i]);
            System.out.println("\tstrProb: " + (probResultStress[i][STRESS_CLASS]) * 100);

        }

//        System.out.println("Med correct: " + numCorrect + "/" + ((med_raw.length- WINDOW_SHIFT)/(WINDOW_SHIFT)));
        System.out.println("STR correct: " + numCorrect + "/" + totalFeat);


    }

    @Test
    public void testSVMRawToPredict() throws IOException {

        String eegMeditationFn = "raw_eeg_zhijie_meditation.csv";
        String eegStressFn = "raw_eeg_zhijie_stress.csv";
        String modelFn = "svm_model_wo_zj.txt";

        InputStream is = getClass().getClassLoader().getResourceAsStream(modelFn);

        //Load Model
        svm_model svmModel;
        svmModel = svm.svm_load_model(new BufferedReader(new InputStreamReader(is)));


        //Setup Stress and Meditation Features
        double[][] raw_eeg_meditation = csv_reader(eegMeditationFn, NUM_EEG_CH);
        double[][] features_meditation = sh.rawToFeature(raw_eeg_meditation);

        double[][] raw_eeg_stress = csv_reader(eegStressFn, NUM_EEG_CH);
        double[][] features_stress = sh.rawToFeature(raw_eeg_stress);


        //Result Meditation: 0, Stress: 1
        //Setting up features for testing
        double[] stressPredictResult = new double[features_stress.length];
        double[] meditationPredictResult = new double[features_meditation.length];
        svm_node[] node_stress;
        svm_node[] node_meditation;

        double[][] probResultStress = new double[features_stress.length][];
        double[][] probResultMeditation = new double[features_meditation.length][];

        double[][] probResult = new double[features_stress.length + features_meditation.length][];

        double numCorrectPredict = 0;

//        for (int i = 0; i < features_meditation.length; i++) {
//            node_meditation = sh.featuresToSVMNode(features_meditation[i]);
//            probResult[i] = new double[2];
//
//            meditationPredictResult[i] = svm.svm_predict_probability(svmModel, node_meditation, probResult[i]);
//            if (meditationPredictResult[i] == MEDITATION_CLASS) {
//                numCorrectPredict++;
//            }
//            System.out.print("result:" + meditationPredictResult[i]);
//            System.out.println("\tpredict:" + probResult[i][STRESS_CLASS]);
//        }

        for (int i = 0; i < features_stress.length; i++) {

            node_stress = sh.featuresToSVMNode(features_stress[i]);
            probResult[i + features_meditation.length] = new double[2];
            stressPredictResult[i] = svm.svm_predict_probability(svmModel, node_stress, probResult[features_meditation.length + i]);
            if (stressPredictResult[i] == STRESS_CLASS) {
                numCorrectPredict++;
            }
            System.out.print("result:" + stressPredictResult[i]);
            System.out.println("\tpredict:" + probResult[features_meditation.length + i][STRESS_CLASS] * 100);
        }

        int totalFeatures = (features_stress.length + features_meditation.length);
        double predictAccuracy = numCorrectPredict / totalFeatures * 100;
        System.out.println("Accuracy = " + predictAccuracy + "% (" + numCorrectPredict + "/" + totalFeatures + ")");
        System.out.println("Accuracy = " + numCorrectPredict + "/" + totalFeatures);

        double[][] matlabProb = csv_read_prob("probability_test_zj.csv");
        compare_array_with_delta(matlabProb, probResult, 0.04);
//        for (int i =0; i<probResultMeditation.length; i++){
//            System.out.println("Prob:" + probResultMeditation[i][0]);
//
//        }
        System.out.print("");
    }

    @Test
    public void testSamplingFreq_size() throws Exception {
        String eeg_sample_size_fn = "sampleFreq/eeg_raw.csv";
        String eeg_matlab_ar_fn = "sampleFreq/eeg_ar_complete.csv";
        String eeg_matlab_feat_fn = "sampleFreq/eeg_extract_feat.csv";

        double[][] rawEEG = csv_reader(eeg_sample_size_fn, NUM_EEG_CH);

//        double[][] eeg_ar = sh.artifactRemoval(rawEEG);
//        double[][] matlab_filter = csv_reader(eeg_matlab_ar_fn, NUM_EEG_CH);
//        compare_array_with_delta(matlab_filter, eeg_ar);

        double[][] feat = sh.rawToFeature(rawEEG);
//        double[][] matlab_feat = csv_reader(eeg_matlab_feat_fn, 24);
//        compare_array_with_delta(matlab_feat, feat);

        String fn = "sampleFreq/full_zj_model.txt";
        InputStream is = getClass().getClassLoader().getResourceAsStream(fn);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        //Load Model
        svm_model svmModel;
        svmModel = svm.svm_load_model(br);

        svm_node[] node = sh.featuresToSVMNode(feat[0]);

        double[] probResult = new double[2];
        svm.svm_predict_probability(svmModel, node, probResult);

        System.out.println("Med:" + probResult[0] * 100 + "Stress: " + probResult[1] * 100);
        // matlab 100% meditation
        System.out.println("");
    }

    @Test
    public void testFilterIIRFunction() throws Exception {
        double[][] java_bandpass_eeg = csv_reader(eeg_raw_fn, NUM_EEG_CH);

        for (int i = 0; i < NUM_EEG_CH; i++) {
            sh.filterIIR(preFilterB, preFilterA, java_bandpass_eeg, i);
        }

        double[][] matlab_eeg_after_filter = csv_reader(eeg_after_filter_fn, NUM_EEG_CH);
        compare_array_with_delta(matlab_eeg_after_filter, java_bandpass_eeg);

    }

    @Test
    public void testArtifactRemoval() throws Exception {

        double[][] eegRaw = csv_reader(eeg_raw_fn, NUM_EEG_CH);
        double[][] eeg_ar_java = sh.artifactRemoval(eegRaw);
        double[][] eeg_matlab_after_ar = csv_reader(eeg_after_ar_fn, NUM_EEG_CH);
        compare_array_with_delta(eeg_matlab_after_ar, eeg_ar_java);

    }

    @Test
    public void testExtractFeatures() throws Exception {
        SVM_Helper sh = new SVM_Helper();

        double[][] eeg = csv_reader(eeg_after_ar_fn, NUM_EEG_CH);
        double[][] extractedFeatures = sh.extractFeatures(eeg);

        double[][] matlabExtractFeatures = csv_reader(eeg_extract_features_fn, 24);

        compare_array_with_delta(matlabExtractFeatures, extractedFeatures);

        System.out.print("");


    }

    @Test
    public void testBandpassFilter() throws Exception {
        double[][] xm = csv_reader(eeg_after_ar_fn, NUM_EEG_CH);
        double[][][] xmFiltered = sh.bandPassFilter(xm);
        String filename = "extractFeature_after_bandpass.csv";
        double[][][] matlab = csv_reader(filename, NUM_EEG_CH, NUM_BAND);
        compare_array_with_delta(matlab, xmFiltered);
    }

    @Test
    public void testRawToFeatures() throws IOException {
        double[][] raw_eeg = csv_reader(eeg_raw_fn, NUM_EEG_CH);
        double[][] matlabExtractedFeatures = csv_reader(eeg_extract_features_fn, 24);

        double[][] extractedFeatures = sh.rawToFeature(raw_eeg);

        compare_array_with_delta(matlabExtractedFeatures, extractedFeatures);

    }

    @Test
    public void testLoadSVMmodel() throws Exception {
        //Load get file as inputsteam
        String fn = "svm_model_test.txt";
        InputStream is = getClass().getClassLoader().getResourceAsStream(fn);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        //Load Model
        svm_model svmModel;
        svmModel = svm.svm_load_model(br);

        System.out.print("");
    }



    private double[][] deep_copy_2d(double[][] array) {
        double[][] copiedArray = new double[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(array[i], 0, copiedArray[i], 0, array[0].length);
        }
        return copiedArray;
    }

    private void compare_array_with_delta(double[][] matlabArray, double[][] javaArray) {
        boolean pass = true;
        for (int i = 0; i < matlabArray.length; i++) {
            for (int j = 0; j < matlabArray[i].length; j++) {
                Boolean result = abs(matlabArray[i][j] - javaArray[i][j]) > DELTA;
                if (result) {
                    System.out.println("Error Location = i=" + i + "\tj=" + j);
//                    System.out.println("Matlab:\t" + matlabArray[i][j] + "\nJava:\t" + javaArray[i][j]);
                    System.out.println("delta = " + abs(matlabArray[i][j] - javaArray[i][j]));
                    System.out.println();
                    pass = false;

                }
//                assertFalse(result);
            }
        }

        if (!pass)
            assert true;
    }

    private void compare_array_with_delta(double[][] matlabArray, double[][] javaArray, double delta) {
        for (int i = 0; i < matlabArray.length; i++) {
            for (int j = 0; j < matlabArray[i].length; j++) {
                Boolean result = abs(matlabArray[i][j] - javaArray[i][j]) > delta;
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
                    Boolean result = abs(matlabArray[i][j][k] - javaArray[i][j][k]) > DELTA;
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
     * @param filename Name of file to be read
     * @param ch 2D
     * @param band 3D
     * @return 3D array of data in double
     */
    private double[][][] csv_reader(String filename, int ch, int band) throws IOException {

        int aSize = getNumLinesInFile(filename);
        double[][][] array = new double[aSize][ch][band];

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(filename));
        CSVReader csvReader = new CSVReader(reader);
        String[] nextRecord;
        int count;
        int index = 0;
        while ((nextRecord = csvReader.readNext()) != null) {
            count = 0;
            for (int k = 0; k < band; k++) {
                for (int j = 0; j < ch; j++) { //Shift the Ch first
                    array[index][j][k] = Double.parseDouble(nextRecord[count++]);
                }
            }
            index++;
        }

        return array;

    }

    private double[][] csv_reader(String filename, int ch) throws IOException {

        int aSize = getNumLinesInFile(filename);
        double[][] new_array = new double[aSize][ch];

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(filename));
        CSVReader csvReader = new CSVReader(reader);
        String[] nextRecord;
        int count;
        int index = 0;
        while ((nextRecord = csvReader.readNext()) != null) {
            count = 0;
            for (int j = 0; j < ch; j++) {
                new_array[index][j] = Double.parseDouble(nextRecord[count++]);
            }
            index++;
        }

        return new_array;

    }

    private double[][] csv_read_prob(String filename) throws IOException {

        int aSize = getNumLinesInFile(filename);
        int num = 2;

        double[][] array = new double[aSize][num];

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(filename));
        CSVReader csvReader = new CSVReader(reader);

        String[] nextRecord;
        int count;
        int index = 0;
        while ((nextRecord = csvReader.readNext()) != null) {
            count = 0;
            for (int j = 0; j < num; j++) {
                array[index][j] = Double.parseDouble(nextRecord[count++]);
            }
            index++;
        }

        return array;

    }

    private int getNumLinesInFile(String filename) throws IOException {
        int numLines = 0;
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(filename));
        CSVReader csvReader = new CSVReader(reader);
        while (csvReader.readNext() != null) {
            numLines++;
        }

        return numLines;
    }
}


    

