package com.testproject.service.audio;

import android.util.Log;

import com.jlibrosa.audio.process.AudioFeatureExtraction;
import com.testproject.service.util.Stat;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

public class JLib {

    public static void test() {
    }

    private static final double EPS = 1e-10;
    private static final int TOP_DB = 80;

    public static float[] toFloat(byte[] b) {
        //Log.i("JLib", "Got byte array of size " + b.length);
        ShortBuffer shortBuffer = ByteBuffer
                .wrap(b)
                .asShortBuffer();
        //Log.i("JLib", "Got short array of size " + shortBuffer.position());
        //Log.i("JLib", "Short buffer capacity " + shortBuffer.capacity());
        float[] floats = new float[shortBuffer.capacity() / 2];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = shortBuffer.get(2 * i) + shortBuffer.get(2 * i + 1);
            floats[i] /= 2 * Short.MAX_VALUE;
        }
        shortBuffer.clear();
        return floats;
    }

    public static double getBPM(float[] y) {
        //double[][] stft = getSTFT(y, 2048, 512);
        double[][] mel = getMelGram(y);
        Log.i("JLib", "Mel ready");
        double[][] lgs = scaleToDB(mel);
        float[] nov = getNovelty(lgs);
        Log.i("JLib", "Nov ready");
        double[][] tmp = getSTFT(nov, 512, 1);
        Log.i("JLib", "Tmp ready");
        int[] frq = Stat.argmax(Arrays.copyOfRange(tmp, 5, 40));
        double f = Stat.freq(frq, 5, 40);
        Log.i("JLib", String.valueOf((f + 5) * 44100 * 60 / 2048 * 4 / 512));
        return (f + 5) * 44100 * 60 / 2048 * 4 / 512;
    }

    public static double[][] getMelGram(float[] y) {
        return new AudioFeatureExtraction().melSpectrogram(y);
    }

    public static double[] getNovelty(double[] y) {
        double[] nov = new double[y.length - 1];
        for (int i = 0; i < y.length - 1; i++) {
            nov[i] = Math.max(0, y[i + 1] - y[i]);
        }
        return nov;
    }

    public static float[] getNovelty(double[][] y) {
        float[] meanNov = new float[y[0].length - 1];
        for (double[] f : y) {
            double[] nov = getNovelty(f);
            for (int i = 0; i < nov.length; i++) {
                meanNov[i] += nov[i];
            }
        }
        for (int i = 0; i < meanNov.length; i++) {
            meanNov[i] /= y.length;
        }
        return meanNov;
    }

    public static double[][] scaleToDB(double[][] y) {
        double ref = 10 * Math.log10(Stat.max(y) + EPS);
        double[][] log = new double[y.length][y[0].length];
        for (int i = 0; i < log.length; i++) {
            for (int j = 0; j < log[i].length; j++) {
                log[i][j] = 10 * Math.log10(y[i][j] + EPS) - ref;
                if (log[i][j] < -TOP_DB) {
                    log[i][j] = -TOP_DB;
                }
            }
        }
        return log;
    }

    public static double[][] getSTFT(float[] y, int nFFT, int hopLength) {
        AudioFeatureExtraction mfccConvert = new AudioFeatureExtraction();
        mfccConvert.setN_fft(nFFT);
        mfccConvert.setHop_length(hopLength);
        return mfccConvert.extractSTFTFeatures(y);
    }
}
