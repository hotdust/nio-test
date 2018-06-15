package nio;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class TestWriteByFileChannel {

//    public static final int FILE_SIZE = 10 * 1000 * 1000; // 10M
    public static final int FILE_SIZE = 100 * 1000 * 1000; // 100M
    public static final String FILE_PATH = "writefile.txt";
    public static int WRITE_SIZE = 4 * 1000 * 1000; // 10M
    public static int SLEEP_TIME = 15; // 10M
    public static byte BYTE_CONTENT = (byte) 0;
    public static final byte BYTE_CONTENT_1 = (byte) 1;

    ByteBuffer buf = null;

    public void chooseMethod(String[] args) throws IOException, InterruptedException {
        int code = Integer.parseInt(args[0]);

        System.out.println("BYTE_CONTENT:" + BYTE_CONTENT + " SLEEP_TIME:" + SLEEP_TIME);


        switch (code) {
            case 11:
                init(args);
                Buffer buffer = writeBufTwice();
                pretendDoSth(buffer);
                return;
            case 12:
                init(args);
                writeFile();
                return;
            case 13:
                init(args);
                writeFileAndForce();
//                writeFileAndForceNoBigBuff();
                return;
            case 14:
                init(args);
                writeFileAndForceByMMAP();
                return;
            case 24:
                init(args);
                readOneByOneByMMAP();
                return;
            case 241:
                init(args);
                readWholeFileByMMAP();
                return;

            case 31:
//                initForCompare(args);
                writeFileChunkedByFileStream();
                return;
            case 32: //
//                initForCompare(args);
                writeFileChunkedByBuffered();
                return;
            case 33: //
//                initForCompare(args);
                writeFileChunkedByChannel();
                return;
            case 34: //
//                initForCompare(args);
                writeFileChunkedByMMAP();
                return;
            case 35:
                initForCompare(args);
                writeFilePartlyByFileStream();
                return;
            case 36: //
                initForCompare(args);
                writeFilePartlyByBuffered();
                return;
            case 37: //
                initForCompare(args);
                writeFilePartylyByChannel();
                return;
            case 38: //
                initForCompare(args);
                writeFilePartlyWithHeapByMMAP();
                return;
            case 39: //
                initForCompare(args);
                writeFilePartlyWithBufferByMMAP();
                return;
        }
    }

    public void init(String[] args) {
        int data = Integer.parseInt(args[1]);
        int sleepTime = Integer.parseInt(args[2]);

        BYTE_CONTENT = (byte) data;
        SLEEP_TIME = sleepTime;
    }


    public void initForCompare(String[] args) {
        int writeSize = Integer.parseInt(args[1]);
//        writeSize = writeSize * 1000 * 1000;
        writeSize = writeSize * 1000;
        WRITE_SIZE = writeSize;
        System.out.println("WRITE_SIZE:" + WRITE_SIZE);

    }




    public Buffer writeBufTwice() throws IOException, InterruptedException {

        buf = ByteBuffer.allocateDirect(FILE_SIZE);
        for (int i = 0; i < FILE_SIZE; i++) {
            buf.put(BYTE_CONTENT);
        }

        System.out.println("buf is filled first time");
        TimeUnit.SECONDS.sleep(10);

        buf.position(0);
        for (int i = 0; i < FILE_SIZE; i++) {
            buf.put(BYTE_CONTENT_1);
        }
        System.out.println("buf is filled second time");
        TimeUnit.SECONDS.sleep(10);


        return buf;
    }




    public void writeFile() throws IOException, InterruptedException {

        buf = ByteBuffer.allocateDirect(FILE_SIZE);
        for (int i = 0; i < FILE_SIZE; i++) {
            buf.put(BYTE_CONTENT);
        }
        buf.flip();

        System.out.println("buf is filled");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);


//        String filePath = "/Users/iss/store/a.txt";
        RandomAccessFile aFile = new RandomAccessFile(FILE_PATH, "rw");
        FileChannel channel = aFile.getChannel();

        while (buf.hasRemaining()) {
            channel.write(buf);
        }

        System.out.println("buf write cpmplete");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);



    }

    public void writeFileAndForce() throws IOException, InterruptedException {

        buf = ByteBuffer.allocateDirect(FILE_SIZE);
        for (int i = 0; i < FILE_SIZE; i++) {
            buf.put(BYTE_CONTENT);
        }
        buf.flip();

        System.out.println("buf is filled");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);


//        String filePath = "/Users/iss/store/a.txt";
        RandomAccessFile aFile = new RandomAccessFile(FILE_PATH, "rw");
        FileChannel channel = aFile.getChannel();

        while (buf.hasRemaining()) {
            channel.write(buf);
        }

        System.out.println("buf write cpmplete");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);


        channel.force(false);
        System.out.println("force cpmplete");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);
    }

    public void writeFileAndForceNoBigBuff() throws IOException, InterruptedException {

        RandomAccessFile aFile = new RandomAccessFile(FILE_PATH, "rw");
        FileChannel channel = aFile.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1);
        byteBuffer.put(BYTE_CONTENT);
        byteBuffer.flip();
        for (int i = 0; i < FILE_SIZE; i++) {
            channel.write(byteBuffer);
            byteBuffer.position(0);
//            byteBuffer.limit();
        }
        while (buf.hasRemaining()) {
            channel.write(buf);
        }

        System.out.println("buf write cpmplete");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);


        channel.force(false);
        System.out.println("force cpmplete");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);
    }




    public void writeFileAndForceByMMAP() throws IOException, InterruptedException {

        try (FileChannel channel = FileChannel.open(Paths.get(FILE_PATH),
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            System.out.println("file opened");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);

            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);
            System.out.println("file mapped");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);


            for (int i = 0; i < FILE_SIZE; i++) {
                map.put(BYTE_CONTENT);
            }
            System.out.println("file writed");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);


            map.force();
            System.out.println("file forced");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void readOneByOneByMMAP() throws IOException, InterruptedException {

        File file = new File(FILE_PATH);
        long len = file.length();
        System.out.println("read file:" + file + " length:" + len);
        System.out.println("file opened");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);

        try {
            MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file, "r")
                    .getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, len);

            System.out.println("file mapped");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);


            byte b;
            StringBuffer sb = new StringBuffer();
            for (int offset = 0; offset < len; offset++) {
                if (offset % 1000000 == 0) {
                    System.out.println("sb:" + sb.getClass());
                    sb.setLength(0);
                }
                b = mappedByteBuffer.get();
                pretendDoSth(b);
                sb.append(b);
            }
            System.out.println("file readed");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);

        } catch (IOException e) {}

    }


    public void readWholeFileByMMAP() throws IOException, InterruptedException {

        File file = new File(FILE_PATH);
        long len = file.length();
        System.out.println("read file:" + file + " length:" + len);
        System.out.println("file opened");
        TimeUnit.SECONDS.sleep(SLEEP_TIME);

        try {
            MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file, "r")
                    .getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, len);

            System.out.println("file mapped");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);


            byte[] all = new byte[(int)len];
            System.out.println("buffer allocated");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);

            mappedByteBuffer.get(all, 0, (int)len);
//            for (int offset = 0; offset < len; offset++) {
//                byte b = mappedByteBuffer.get();
//                pretendDoSth(b);
//            }
            System.out.println("file readed");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);

        } catch (IOException e) {}

    }


    public void readTwiceByMMAP() throws IOException, InterruptedException {

        File file = new File(FILE_PATH);
        long len = file.length();

        try {
            MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file, "r")
                    .getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, len);
            for (int offset = 0; offset < len; offset++) {
                byte b = mappedByteBuffer.get();
                pretendDoSth(b);
            }

            System.out.println("read first time");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);


            MappedByteBuffer mappedByteBuffer1 = new RandomAccessFile(file, "r")
                    .getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, len);
            for (int offset = 0; offset < len; offset++) {
                byte b = mappedByteBuffer1.get();
                pretendDoSth(b);
            }

            System.out.println("read second time");
            TimeUnit.SECONDS.sleep(SLEEP_TIME);

        } catch (IOException e) {}

    }

    public void writeFileChunkedByFileStream() throws IOException {
        long start = System.currentTimeMillis();

        File file = new File(FILE_PATH);
        FileOutputStream out = new FileOutputStream(file);

        byte[] bytes = new byte[FILE_SIZE];
        for (int i = 0; i < FILE_SIZE; i++) {
            bytes[i] = BYTE_CONTENT;
        }

        out.write(bytes);
        out.flush();
        out.close();

        System.out.println("writeFileChunkedByFileStream:" + (System.currentTimeMillis() - start));
    }


    public void writeFileChunkedByBuffered() throws IOException {
        long start = System.currentTimeMillis();

        File file = new File(FILE_PATH);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), FILE_SIZE);

        byte[] bytes = new byte[FILE_SIZE];
        for (int i = 0; i < FILE_SIZE; i++) {
            bytes[i] = BYTE_CONTENT;
        }

        out.write(bytes);
        out.flush();
        out.close();

        System.out.println("writeFileChunkedByBuffered:" + (System.currentTimeMillis() - start));

    }


    public void writeFileChunkedByChannel() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();

        buf = ByteBuffer.allocateDirect(FILE_SIZE);
        for (int i = 0; i < FILE_SIZE; i++) {
            buf.put(BYTE_CONTENT);
        }
        buf.flip();

        RandomAccessFile aFile = new RandomAccessFile(FILE_PATH, "rw");
        FileChannel channel = aFile.getChannel();

        while (buf.hasRemaining()) {
            channel.write(buf);
        }

        channel.force(false);
        System.out.println("writeFileChunkedByChannel:" + (System.currentTimeMillis() - start));

    }



    public void writeFileChunkedByMMAP() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();

        byte[] bytes = new byte[FILE_SIZE];
        for (int i = 0; i < FILE_SIZE; i++) {
            bytes[i] = BYTE_CONTENT;
        }
        System.out.println("buffer prepard time:" + (System.currentTimeMillis() - start));

        try (FileChannel channel = FileChannel.open(Paths.get(FILE_PATH),
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {

            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);
            map.put(bytes);
            map.force();


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("writeFileChunkedByMMAP:" + (System.currentTimeMillis() - start));

    }



    // -----------------------------------------------------------------
    // -------------------    write partly start -----------------------
    // -----------------------------------------------------------------
    public void writeFilePartlyByFileStream() throws IOException {
        long start = System.currentTimeMillis();

        File file = new File(FILE_PATH);
        final FileOutputStream out = new FileOutputStream(file);

//        byte[] bytes = new byte[WRITE_SIZE];
//        int outterCnt = FILE_SIZE / WRITE_SIZE;
//        for (int i = 0; i < outterCnt; i++) {
//            for (int j = 0; j < WRITE_SIZE; j++) {
//                bytes[i] = BYTE_CONTENT;
//            }
//            out.write(bytes);
//        }

//        doWriteByPartlyCompare(false, bytes -> {
//            try {
//                out.write(bytes);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }, null);

        doWriteByPartlyCompare(false, new DoStreamCallBack() {
                    @Override
                    public void doSth(byte[] bytes) {
                        try {
                            out.write(bytes);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                , null);

        out.flush();
        out.close();

        System.out.println("writeFilePartlyByFileStream:" + (System.currentTimeMillis() - start));
    }


    public void writeFilePartlyByBuffered() throws IOException {
        long start = System.currentTimeMillis();

        File file = new File(FILE_PATH);
        final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), WRITE_SIZE);

//        doWriteByPartlyCompare(false, bytes -> {
//            try {
//                out.write(bytes);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }, null);

        doWriteByPartlyCompare(false, new DoStreamCallBack() {
            @Override
            public void doSth(byte[] bytes) {
                try {
                    out.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, null);



        out.flush();
        out.close();

        System.out.println("writeFilePartlyByBuffered:" + (System.currentTimeMillis() - start));

    }


    public void writeFilePartylyByChannel() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();

//        buf = ByteBuffer.allocateDirect(FILE_SIZE);
//        for (int i = 0; i < FILE_SIZE; i++) {
//            buf.put(BYTE_CONTENT);
//        }
//        buf.flip();

        RandomAccessFile aFile = new RandomAccessFile(FILE_PATH, "rw");
        final FileChannel channel = aFile.getChannel();

//        doWriteByPartlyCompare(true, null, buffer -> {
//            try {
//                channel.write(buffer);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });

        doWriteByPartlyCompare(true, null, new DoChannelCallBack() {
            @Override
            public void doSth(ByteBuffer buffer) {
                try {
                    channel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        while (buf.hasRemaining()) {
//            channel.write(buf);
//        }

        channel.force(false);
        System.out.println("writeFilePartylyByChannel:" + (System.currentTimeMillis() - start));

    }



    public void writeFilePartlyWithHeapByMMAP() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();

//        byte[] bytes = new byte[FILE_SIZE];
//        for (int i = 0; i < FILE_SIZE; i++) {
//            bytes[i] = BYTE_CONTENT;
//        }

        try (FileChannel channel = FileChannel.open(Paths.get(FILE_PATH),
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {

            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);

//            doWriteByPartlyCompare(false, bytes -> {
//                map.put(bytes);
//            }, null);

            doWriteByPartlyCompare(false, new DoStreamCallBack() {
                @Override
                public void doSth(byte[] bytes) {
                    map.put(bytes);
                }
            }, null);

//            doWriteByPartlyCompare(true, null, bytes -> {
//                map.put(bytes);
//            });

//            for (int i = 0; i < FILE_SIZE; i++) {
//                map.put(BYTE_CONTENT_1);
//            }

            map.force();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("writeFilePartlyWithHeapByMMAP:" + (System.currentTimeMillis() - start));

    }

    public void writeFilePartlyWithBufferByMMAP() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();

        try (FileChannel channel = FileChannel.open(Paths.get(FILE_PATH),
                StandardOpenOption.READ, StandardOpenOption.WRITE)) {

            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);


            doWriteByPartlyCompare(true, null, new DoChannelCallBack() {
                @Override
                public void doSth(ByteBuffer buffer) {
                    map.put(buffer);
                }
            });


//            for (int i = 0; i < FILE_SIZE; i++) {
//                map.put(BYTE_CONTENT_1);
//            }

            map.force();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("writeFilePartlyWithBufferByMMAP:" + (System.currentTimeMillis() - start));

    }


    private void doWriteByPartlyCompare(boolean isChannel,DoStreamCallBack streamCB, DoChannelCallBack channelCB) {

        int outterCnt = FILE_SIZE / WRITE_SIZE;
        System.out.println("outterCnt:" + outterCnt);

        if (isChannel) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(WRITE_SIZE);
            for (int i = 0; i < outterCnt; i++) {
                for (int j = 0; j < WRITE_SIZE; j++) {
                    buffer.put(BYTE_CONTENT_1);
                }
                buffer.flip();

                while (buffer.hasRemaining()) {
                    channelCB.doSth(buffer);
                }
                buffer.clear();
            }
        } else {
            byte[] bytes = new byte[WRITE_SIZE];
            for (int i = 0; i < outterCnt; i++) {
                for (int j = 0; j < WRITE_SIZE; j++) {
                    bytes[j] = BYTE_CONTENT_1;
                }
                streamCB.doSth(bytes);
//            System.out.println("outter loop:" + i);
            }
        }

    }

    interface DoStreamCallBack {
        void doSth(byte[] bytes);
    }

    interface DoChannelCallBack {
        void doSth(ByteBuffer buffer);
    }




    // -----------------------------------------------------------------
    // -------------------    write partly end -------------------------
    // -----------------------------------------------------------------




    // -----------------------------------------------------------------
    // -------------------    read partly start -------------------------
    // -----------------------------------------------------------------


    // -----------------------------------------------------------------
    // -------------------    read partly end -------------------------
    // -----------------------------------------------------------------


    private void pretendDoSth(Buffer buffer) {
        // actually nth to do. just cheat compiler
    }

    private void pretendDoSth(byte buffer) {
        // actually nth to do. just cheat compiler
    }

    private void pretendDoSth(byte[] buffer) {
        // actually nth to do. just cheat compiler
    }
}
