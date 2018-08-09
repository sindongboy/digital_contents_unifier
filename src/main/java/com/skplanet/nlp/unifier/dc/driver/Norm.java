package com.skplanet.nlp.unifier.dc.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 11/4/15
 */
public class Norm {
    public static double[] scaleValues(double[] vals) {
        double[] result = new double[vals.length];
        double min = minArray(vals);
        double max = maxArray(vals);
        double scaleFactor = max - min;
        // scaling between [0..1] for starters. Will generalize later.
        for (int x = 0; x < vals.length; x++) {
            result[x] = ((vals[x] - min) / scaleFactor);
        }
        return result;
    }

    // The standard collection classes don't have array min and max.
    public static double minArray(double[] vals) {
        double min = vals[0];
        for (int x = 1; x < vals.length; x++) {
            if (vals[x] < min) {
                min = vals[x];
            }
        }
        return min;
    }

    public static double maxArray(double[] vals) {
        double max = vals[0];
        for (int x = 1; x < vals.length; x++) {
            if (vals[x] > max) {
                max = vals[x];
            }
        }
        return max;
    }

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(new File("/Users/sindongboy/Dropbox/Documents/workspace/naver-land-crawler/meta/ourtown/aaa")));
        List<Double> entryList = new ArrayList<Double>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }
            entryList.add(Double.parseDouble(line));
        }
        reader.close();
        double[] samples = new double[entryList.size()];
        int index = 0;
        for (Double num : entryList) {
            samples[index] = num.doubleValue();
            index++;
        }

        double[] result = scaleValues(samples);
        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i]);
        }


    }
}
