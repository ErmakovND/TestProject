package com.testproject.service.util;

public class Stat {
    public static double max(double[][] y) {
        double max = Double.MIN_VALUE;
        for (double[] f : y) {
            for (double d : f) {
                if (d > max) {
                    max = d;
                }
            }
        }
        return max;
    }

    public static int[] argmax(double[][] y) {
        int[] arg = new int[y[0].length];
        for (int i = 0; i < arg.length; i++) {
            double max = Double.MIN_VALUE;
            int maxInd = -1;
            for (int j = 0; j < y.length; j++) {
                if (y[j][i] > max) {
                    max = y[j][i];
                    maxInd = j;
                }
            }
            arg[i] = maxInd;
        }
        return arg;
    }

    public static int argmax(int[] y) {
        int max = Integer.MIN_VALUE;
        int maxInd = -1;
        for (int i = 0; i < y.length; i++) {
            if (y[i] > max) {
                max = y[i];
                maxInd = i;
            }
        }
        return maxInd;
    }

    public static double mean(int[] y) {
        double sum = 0;
        for (int i : y) {
            sum += i;
        }
        return sum / y.length;
    }

    public static int freq(int[] y, int min, int max) {
        int[] count = new int[max - min + 1];
        for (int i : y) {
            count[i - min]++;
        }
        return argmax(count) + min;
    }
}
