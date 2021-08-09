package com.testproject.service.audio;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.testproject.service.util.ByteArray;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioUtil {

    public static byte[] load(String path) throws IOException, InterruptedException {

        MediaExtractor extractor = getExtractor(path);
        MediaFormat format = getFormat(extractor);
        MediaCodec decoder = getDecoder(format);

        ByteArray res = new ByteArray();

        Runnable readingTask = () -> {
            boolean EOS = false;
            while (!EOS) {
                EOS = putIntoCodec(extractor, decoder);
            }
        };
        Thread readingThread = new Thread(readingTask);

        Runnable writingTask = () -> {
            boolean EOS = false;
            while (!EOS) {
                EOS = getFromCodec(res, decoder);
            }
        };
        Thread writingThread = new Thread(writingTask);

        decoder.start();

        readingThread.start();
        writingThread.start();

        readingThread.join();
        writingThread.join();

        decoder.stop();
        decoder.release();
        extractor.release();

        Log.i("AudioLoad", res.size() + " bytes in total");

        return res.array();
    }

    private static MediaExtractor getExtractor(String path) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        Log.i("Extractor", "Ready");
        extractor.setDataSource(path);
        Log.i("Extractor", "Instantiated");
        extractor.selectTrack(0);
        return extractor;
    }

    private static MediaFormat getFormat(MediaExtractor extractor) {
        return extractor.getTrackFormat(0);
    }

    private static MediaCodec getDecoder(MediaFormat format) throws IOException {
        MediaCodecList codecs = new MediaCodecList(MediaCodecList.ALL_CODECS);
        String codecName = codecs.findDecoderForFormat(format);
        MediaCodec codec = MediaCodec.createByCodecName(codecName);
        codec.configure(format, null, null, 0);
        return codec;
    }

    private static boolean putIntoCodec(MediaExtractor extractor, MediaCodec codec) {
        boolean EOS = false;
        int ipIdx = codec.dequeueInputBuffer(1000);
        if (ipIdx >= 0) {
            ByteBuffer inputBuffer = codec.getInputBuffer(ipIdx);
            int sampleSize = extractor.readSampleData(inputBuffer, 0);
            EOS = sampleSize < 0;
            codec.queueInputBuffer(
                    ipIdx,
                    0,
                    EOS ? 0 : sampleSize,
                    EOS ? 0 : extractor.getSampleTime(),
                    EOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0
            );
            extractor.advance();
        }
        return EOS;
    }

    private static boolean getFromCodec(ByteArray res, MediaCodec codec) {
        boolean EOS = false;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int opIdx = codec.dequeueOutputBuffer(info, 1000);
        if (opIdx >= 0) {
            ByteBuffer outputBuffer = codec.getOutputBuffer(opIdx);
            byte[] bytes = new byte[info.size];
            outputBuffer.get(bytes);
            res.append(bytes);
            codec.releaseOutputBuffer(opIdx, false);
            EOS = (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
        } else if (opIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            MediaFormat outputFormat = codec.getOutputFormat();
            Log.i("Codec", "Output format: " + outputFormat.toString());
        }
        return EOS;
    }
}
