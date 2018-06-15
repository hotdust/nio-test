import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) {


        File file = new File("writefile.txt");
        long len = file.length();

        try {
            MappedByteBuffer map = new RandomAccessFile(file, "rw")
                    .getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, 0, len);


            byte[] bytes = new byte[1];
            ByteBuffer getBuffer = map.get(bytes);

            System.out.println("1 bytes[0]：" + bytes[0]);
            bytes[0] = (byte)3;
            System.out.println("2 bytes[0]：" + bytes[0]);
            System.out.println("1 getBuffer.get(0)：" + getBuffer.get(0));
//            map.put(0, (byte) 4);
            getBuffer.put(0, (byte) 1);
            System.out.println("2 getBuffer.get(0)：" + getBuffer.get(0));




            System.out.println("complete");

        } catch (IOException e) {
        }



//        try (FileChannel channel = FileChannel.open(file.getPath(),
//                StandardOpenOption.READ, StandardOpenOption.WRITE)) {
//
//            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, file.length());
//
//            byte[] bytes = new byte[1];
//            ByteBuffer getBuffer = map.get(bytes);
//
//            System.out.println("1 bytes[0]：" + bytes[0]);
//            bytes[0] = (byte)3;
//            System.out.println("2 bytes[0]：" + bytes[0]);
//            System.out.println("1 getBuffer.get(0)：" + getBuffer.get(0));
//            map.put(0, (byte) 4);
////            getBuffer.put(0, (byte) 4);
//            System.out.println("2 getBuffer.get(0)：" + getBuffer.get(0));
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}

