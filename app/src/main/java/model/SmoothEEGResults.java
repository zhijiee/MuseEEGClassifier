package model;

import java.util.Arrays;

import static constants.AppConstants.defaultPredictValue;
import static constants.AppConstants.smoothPredict;

public class SmoothEEGResults {

    double[] predict;
    int pointer;

    public SmoothEEGResults() {
        pointer = 0;
        predict = new double[smoothPredict];
        Arrays.fill(predict, defaultPredictValue);
    }

    public void add(double prob) {
        predict[pointer] = prob;
        pointer = (pointer + 1) % smoothPredict;
    }

    public double getResult() {
        double sum = 0;
        for (double prob : predict) {
            sum += (prob / (double) smoothPredict);
        }

        return sum;
    }

}
