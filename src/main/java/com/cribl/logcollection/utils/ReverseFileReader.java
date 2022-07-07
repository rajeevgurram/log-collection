package com.cribl.logcollection.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ReverseFileReader {
    private static final int BUFFER_SIZE = 8192;
    private final RandomAccessFile randomAccessFile;
    private final FileChannel fileChannel;
    private ByteBuffer byteBuffer;
    private final String encoding;
    private long filePosition;
    private int bufPosition;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public ReverseFileReader(final File file) throws IOException {
        randomAccessFile = new RandomAccessFile(file, "r");
        fileChannel = randomAccessFile.getChannel();
        filePosition = randomAccessFile.length();
        this.encoding = "UTF-8";
    }

    public void close() {
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readLine() throws IOException {
        byte c;
        while (true) {
            if (bufPosition < 0) {
                if (filePosition == 0) {
                    if (byteArrayOutputStream == null) {
                        return null;
                    }
                    String line = bufToString();
                    byteArrayOutputStream = null;
                    return line;
                }

                long start = Math.max(filePosition - BUFFER_SIZE, 0);
                long end = filePosition;
                long len = end - start;

                byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, start, len);
                bufPosition = (int) len;
                filePosition = start;

                // Ignore Empty New Lines
                c = byteBuffer.get(--bufPosition);
                if (c == '\r' || c == '\n')
                    while (bufPosition > 0 && (c == '\r' || c == '\n')) {
                        bufPosition--;
                        c = byteBuffer.get(bufPosition);
                    }
                if (!(c == '\r' || c == '\n'))
                    bufPosition++;// IS THE NEW LENE
            }

            /*
             * This will ignore all blank new lines.
             */
            while (bufPosition-- > 0) {
                c = byteBuffer.get(bufPosition);
                if (c == '\r' || c == '\n') {
                    // skip \r\n
                    while (bufPosition > 0 && (c == '\r' || c == '\n')) {
                        c = byteBuffer.get(--bufPosition);
                    }
                    // restore cursor
                    if (!(c == '\r' || c == '\n'))
                        bufPosition++;// IS THE NEW Line
                    return bufToString();
                }
                byteArrayOutputStream.write(c);
            }
        }
    }

    private String bufToString() throws UnsupportedEncodingException {
        if (byteArrayOutputStream.size() == 0) {
            return "";
        }

        byte[] bytes = byteArrayOutputStream.toByteArray();
        for (int i = 0; i < bytes.length / 2; i++) {
            byte t = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = t;
        }

        byteArrayOutputStream.reset();
        if (encoding != null)
            return new String(bytes, encoding);
        else
            return new String(bytes);
    }
}
