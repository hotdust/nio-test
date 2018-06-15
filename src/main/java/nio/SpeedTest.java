package nio;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SpeedTest {
    private static final String INPUT_FILE_PATH = "writefile.txt";
    private static final String OUTPUT_FILE_PATH = "writefile_copy.txt";

    public static void main(String[] args) {

        if (args.length == 0) {
            all();
        } else {
            int code = Integer.parseInt(args[0]);
            switch (code) {
                case 1:
                    long ioStreamTime1 = ioStreamCopy();
                    System.out.println("io stream copy:" + ioStreamTime1);
                    return;
                case 2:
                    long ioStreamTime2 = bufferedStreamCopy();
                    System.out.println("buffered stream copy:" + ioStreamTime2);
                    return;
                case 3:
                    long ioStreamTime3 = nioStreamCopy();
                    System.out.println("nio stream copy:" + ioStreamTime3);
                    return;
                case 4:
                    long ioStreamTime4 = nioMemoryStreamCopy();
                    System.out.println("nio memory stream copy:" + ioStreamTime4);
                    return;
            }
        }


    }

    public static void all() {
        long ioStreamTime1 = ioStreamCopy();
        System.out.println("io stream copy:" + ioStreamTime1);

        long ioStreamTime2 = bufferedStreamCopy();
        System.out.println("buffered stream copy:" + ioStreamTime2);

        long ioStreamTime3 = nioStreamCopy();
        System.out.println("nio stream copy:" + ioStreamTime3);

        long ioStreamTime4 = nioMemoryStreamCopy();
        System.out.println("nio memory stream copy:" + ioStreamTime4);
    }

    private static long ioStreamCopy() {
        System.out.println("ioStreamCopy executed");
        long costTime = -1;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            long startTime = System.currentTimeMillis();
            is = new FileInputStream(INPUT_FILE_PATH);
            os = new FileOutputStream(OUTPUT_FILE_PATH);
            int read = is.read();
            while (read != -1) {
                os.write(read);
                read = is.read();
            }
            long endTime = System.currentTimeMillis();
            costTime = endTime - startTime;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return costTime;
    }

    private static long bufferedStreamCopy() {
        System.out.println("bufferedStreamCopy executed");

        long costTime = -1;
        FileReader reader = null;
        FileWriter writer = null;
        try {
            long startTime = System.currentTimeMillis();
            reader = new FileReader(INPUT_FILE_PATH);
            writer = new FileWriter(OUTPUT_FILE_PATH);
            int read = -1;
            while ((read = reader.read()) != -1) {
                writer.write(read);
            }
            writer.flush();
            long endTime = System.currentTimeMillis();
            costTime = endTime - startTime;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return costTime;
    }

    private static long nioStreamCopy() {
        System.out.println("nioStreamCopy executed");

        long costTime = -1;
        FileInputStream is = null;
        FileOutputStream os = null;
        FileChannel fi = null;
        FileChannel fo = null;
        try {
            long startTime = System.currentTimeMillis();
            is = new FileInputStream(INPUT_FILE_PATH);
            os = new FileOutputStream(OUTPUT_FILE_PATH);
            fi = is.getChannel();
            fo = os.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                buffer.clear();
                int read = fi.read(buffer);
                if (read == -1) {
                    break;
                }
                buffer.flip();
                fo.write(buffer);
            }
            long endTime = System.currentTimeMillis();
            costTime = endTime - startTime;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return costTime;
    }

    private static long nioMemoryStreamCopy() {
        System.out.println("nioStreamCopy executed");

        long costTime = -1;
        FileInputStream is = null;
        //映射文件输出必须用RandomAccessFile
        RandomAccessFile os = null;
        FileChannel fi = null;
        FileChannel fo = null;
        try {
            // TODO: 2018/6/6 如果文件没有，就创建。
            long startTime = System.currentTimeMillis();
            is = new FileInputStream(INPUT_FILE_PATH);
            os = new RandomAccessFile(OUTPUT_FILE_PATH, "rw");
            fi = is.getChannel();
            fo = os.getChannel();
            IntBuffer iIb = fi.map(FileChannel.MapMode.READ_ONLY, 0, fi.size()).asIntBuffer();
            IntBuffer oIb = fo.map(FileChannel.MapMode.READ_WRITE, 0, fo.size()).asIntBuffer();
            while (iIb.hasRemaining()) {
                int read = iIb.get();
                oIb.put(read);
            }
            long endTime = System.currentTimeMillis();
            costTime = endTime - startTime;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return costTime;
    }
}